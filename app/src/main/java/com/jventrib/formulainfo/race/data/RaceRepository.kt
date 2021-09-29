package com.jventrib.formulainfo.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.formulainfo.common.utils.emptyListToNull
import com.jventrib.formulainfo.race.data.db.*
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.data.remote.WikipediaService
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.FullRaceResult
import com.jventrib.formulainfo.race.model.mapper.*
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import logcat.logcat

class RaceRepository(
    private val raceDao: RaceDao,
    private val circuitDao: CircuitDao,
    private val raceResultDao: RaceResultDao,
    private val driverDao: DriverDao,
    private val constructorDao: ConstructorDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {
    private val raceStore: Store<Int, List<RaceFull>> =
        StoreBuilder.from(
            Fetcher.ofFlow { season -> raceRemoteDataSource.getRacesFlow(season) },
            SourceOfTruth.of(
                reader = { season ->
                    raceDao.getSeasonRaces(season)
                        .completeMissing({ it.circuit.location.flag }) {
                            circuitDao.insert(getCircuitWithFlag(it))
                        }
                        .emptyListToNull()

                },
                writer = { _: Int, races: List<RaceRemote> ->
                    raceDao.insertAll(RaceMapper.toEntity(races))
                    circuitDao.insertAll(RaceCircuitMapper.toEntity(races))
                },
                deleteAll = {
                    withContext(Dispatchers.IO) {
                        raceDao.deleteAll()
                        circuitDao.deleteAll()
                    }
                }
            )
        ).build()

    fun getRace(r: RaceFull) = flow {
        emit(r)
        if (r.circuit.imageUrl == null) {
            val circuitWithImage = r.circuit.copy(
                imageUrl = raceRemoteDataSource.getCircuitImage(
                    r.circuit.url,
                    500
                )
            )
            circuitDao.insert(circuitWithImage)
            emit(r.copy(circuit = circuitWithImage))
        }
    }

//    private val raceResultStore: Store<SeasonRace, List<RaceResultFull>> =
//        StoreBuilder.from(
//            Fetcher.ofFlow { seasonRace ->
//                raceRemoteDataSource.getRaceResultsFlow(seasonRace.season, seasonRace.round)
//            },
//            SourceOfTruth.of(
//                reader = { seasonRace ->
//                    raceResultDao.getRaceResultsFull(seasonRace.season, seasonRace.round)
//                        .complete({ it.driver.image }) {
//                            driverDao.insert(getDriverWithImage(it))
//                        }
//                        .complete({ it.constructor.image }) {
//                            constructorDao.insert(getConstructorWithImage(it))
//                        }
//                        .emptyListToNull()
//                },
//                writer = { _: SeasonRace, raceResultRemotes: List<RaceResultRemote> ->
//                    driverDao.insertAll(RaceResultDriverMapper.toEntity(raceResultRemotes))
//                    constructorDao.insertAll(RaceResultConstructorMapper.toEntity(raceResultRemotes))
//                    raceResultDao.insertAll(RaceResultMapper.toEntity(raceResultRemotes))
//                },
//                deleteAll = {
//                    withContext(Dispatchers.IO) {
//                        driverDao.deleteAll()
//                        constructorDao.deleteAll()
//                        raceResultDao.deleteAll()
//                    }
//                }
//            )
//        ).build()



    fun getAllRaces(season: Int): Flow<StoreResponse<List<RaceFull>>> {
        return raceStore.stream(StoreRequest.cached(season, false))
            .distinctUntilChangedBy { sr -> sr.dataOrNull() }
    }

    fun getRace(season: Int, round: Int): Flow<RaceFull> {
        return getAllRaces(season)
            .transform { storeResponse ->
                if (storeResponse is StoreResponse.Data) {
                    val first = storeResponse.requireData().first { it.race.round == round }
                    emit(first)
                }
            }
            .flatMapLatest { getRace(it) }
    }

    fun getRaceResults(
        season: Int,
        round: Int
    ): Flow<StoreResponse<List<FullRaceResult>>> =
        raceResultDao.getFullRaceResults(season, round)
            .distinctUntilChanged()
            .transform { data ->
                logcat { "Looking for FullRaceResults in DB" }
                if (data.isEmpty()) {
                    logcat { "No FullRaceResults in DB, fetching from API" }
                    raceRemoteDataSource.getRaceResults(season, round).also {
                        logcat { "Fetched RaceResults from API: $it" }
                        driverDao.insertAll(RaceResultDriverMapper.toEntity(it)
                            .also { logcat { "Inserting Drivers $it" } })
                        constructorDao.insertAll(RaceResultConstructorMapper.toEntity(it)
                            .also { logcat { "Inserting Constructors $it" } })
                        raceResultDao.insertAll(RaceResultMapper.toEntity(season, round, it)
                            .also { logcat { "Inserting Results $it" } })
                    }
                } else {
                    logcat { "Got ${data.size} FullRaceResults from DB" }
                    emit(data)
                }
            }
            .completeMissing({ it.driver.image }) {
                logcat { "Completing driver ${it.driver.code} with image" }
                driverDao.insert(getDriverWithImage(it))
                delay(1000)
            }
//            .completeMissing({ it.constructor.image }) {
//                logcat { "Completing constructor ${it.constructor.id} with image" }
//                constructorDao.insert(getConstructorWithImage(it))
//            }
            .map { StoreResponse.Data(it, ResponseOrigin.SourceOfTruth) }


    suspend fun refresh() {
        raceStore.clearAll()
//        raceResultStore.clearAll()
    }

    private fun <T : List<U>, U> Flow<T>.completeMissing(
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform { list ->
            emit(list)
            list.firstOrNull { attr(it) == null }?.let { action(it) }
        }.distinctUntilChanged()


    private suspend fun getCircuitWithFlag(raceFull: RaceFull) = raceFull.circuit.copy(
        location = raceFull.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                raceFull.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(fullRaceResult: FullRaceResult) =
        fullRaceResult.driver.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                fullRaceResult.driver.url, 200, WikipediaService.Licence.FREE
            ) ?: "NONE"
        )

    private suspend fun getConstructorWithImage(result: FullRaceResult) =
        result.constructor.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                result.constructor.url, 200,
            ) ?: "NONE"
        )

}

