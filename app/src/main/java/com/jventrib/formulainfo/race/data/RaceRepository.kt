package com.jventrib.formulainfo.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.formulainfo.common.utils.emptyListToNull
import com.jventrib.formulainfo.race.data.db.*
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.data.remote.WikipediaService
import com.jventrib.formulainfo.race.model.db.FullRaceResult
import com.jventrib.formulainfo.race.model.db.FullRace
import com.jventrib.formulainfo.race.model.mapper.*
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import logcat.LogPriority
import logcat.logcat

class RaceRepository(
    roomDb: AppRoomDatabase,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {
    private val raceDao: RaceDao = roomDb.raceDao()
    private val circuitDao: CircuitDao = roomDb.circuitDao()
    private val raceResultDao: RaceResultDao = roomDb.raceResultDao()
    private val driverDao: DriverDao = roomDb.driverDao()
    private val constructorDao: ConstructorDao = roomDb.constructorDao()

    data class SeasonRace(val season: Int, val round: Int)

    fun getRaces(season: Int): Flow<StoreResponse<List<FullRace>>> {
        return raceStore.stream(StoreRequest.cached(season, false)).onEach {
            logcat(LogPriority.VERBOSE) { "Races Response: $it" }
        }
    }

    fun getRaceResults(season: Int, round: Int): Flow<StoreResponse<List<FullRaceResult>>> =
        raceResultStore.stream(StoreRequest.cached(SeasonRace(season, round), false)).onEach {
            logcat(LogPriority.VERBOSE) { "RaceResults Response: $it" }
        }

    fun getRace(season: Int, round: Int): Flow<FullRace> {
        return getRaces(season)
            .transform { storeResponse ->
                if (storeResponse is StoreResponse.Data) {
                    val first = storeResponse.requireData().first { it.race.round == round }
                    emit(first)
                }
            }
            .flatMapLatest { getRace(it) }
    }

    suspend fun refresh() {
        raceStore.clearAll()
        raceResultStore.clearAll()
    }

    private val raceStore: Store<Int, List<FullRace>> =
        StoreBuilder.from(
            Fetcher.of { season ->
                logcat { "No FullRace in DB, fetching from API" }
                raceRemoteDataSource.getRaces(season).also {
                    logcat { "Fetched Races from API: $it" }
                }
            },
            SourceOfTruth.of(
                reader = { season ->
                    logcat { "Getting FullRaces from DB" }
                    raceDao.getSeasonRaces(season)
                        .onEach {
                            logcat { "Got ${it.size} FullRaces from DB" }
                        }
                        .completeMissing({ it.circuit.location.flag }) {
                            val flag = getCircuitWithFlag(it)
                            logcat { "Completing circuit ${it.circuit.id} with image ${flag.location.flag}" }
                            circuitDao.insert(flag)
                        }
                        .emptyListToNull()

                },
                writer = { _: Int, races: List<RaceRemote> ->
                    raceDao.insertAll(RaceMapper.toEntity(races))
                    circuitDao.insertAll(RaceCircuitMapper.toEntity(races))
                },
                deleteAll = {
                    logcat { "Deleting Races and Circuits" }
                    withContext(Dispatchers.IO) {
                        raceDao.deleteAll()
                        circuitDao.deleteAll()
                    }
                }
            )
        ).build()

    private fun getRace(r: FullRace) = flow {
        emit(r)
        if (r.circuit.imageUrl == null) {
            val circuitWithImage = r.circuit.copy(
                imageUrl = raceRemoteDataSource.getCircuitImage(r.circuit.url, 500)
            )
            circuitDao.insert(circuitWithImage)
            emit(r.copy(circuit = circuitWithImage))
        }
    }

    private val raceResultStore: Store<SeasonRace, List<FullRaceResult>> =
        StoreBuilder.from(
            Fetcher.of { seasonRace ->
                logcat { "No FullRaceResults in DB, fetching from API" }
                raceRemoteDataSource.getRaceResults(seasonRace.season, seasonRace.round).also {
                    logcat { "Fetched RaceResults from API: $it" }
                }
            },
            SourceOfTruth.of(
                reader = { seasonRace ->
                    logcat { "Getting FullRaceResults from DB" }
                    raceResultDao.getFullRaceResults(seasonRace.season, seasonRace.round)
                        .onEach {
                            logcat { "Got ${it.size} FullRaceResults from DB" }
                        }
                        .completeMissing({ it.driver.image }) {
                            logcat { "Completing driver ${it.driver.code} with image" }
                            driverDao.insert(getDriverWithImage(it))
                        }
//                        .completeMissingInList({ it.constructor.image }) {
//                            constructorDao.insert(getConstructorWithImage(it))
//                        }
                        .emptyListToNull()
                },
                writer = { seasonRace: SeasonRace, raceResultRemotes: List<RaceResultRemote> ->
                    driverDao.insertAll(
                        RaceResultDriverMapper.toEntity(raceResultRemotes)
                            .also { logcat { "Inserting Drivers $it" } })
                    constructorDao.insertAll(
                        RaceResultConstructorMapper.toEntity(raceResultRemotes)
                            .also { logcat { "Inserting Constructors $it" } })
                    raceResultDao.insertAll(RaceResultMapper.toEntity(
                        seasonRace.season,
                        seasonRace.round,
                        raceResultRemotes
                    )
                        .also { logcat { "Inserting Results $it" } })
                },
                deleteAll = {
                    logcat { "Deleting Drivers, Constructors and RaceResults" }
                    withContext(Dispatchers.IO) {
                        driverDao.deleteAll()
                        constructorDao.deleteAll()
                        raceResultDao.deleteAll()
                    }
                }
            )
        ).build()


    private fun <T : List<U>, U> Flow<T>.completeMissing(
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform { response ->
            emit(response)
            response.firstOrNull { attr(it) == null }?.let { action(it) }
        }.distinctUntilChanged()


    private suspend fun getCircuitWithFlag(fullRace: FullRace) = fullRace.circuit.copy(
        location = fullRace.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                fullRace.circuit.location.country
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

