package com.jventrib.formulainfo.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.formulainfo.common.utils.emptyListToNull
import com.jventrib.formulainfo.race.data.db.*
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.data.remote.WikipediaService
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import com.jventrib.formulainfo.race.model.mapper.*
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
                        .complete({ it.circuit.location.flag }) {
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

    private val raceResultStore: Store<SeasonRace, List<RaceResultFull>> =
        StoreBuilder.from(
            Fetcher.ofFlow { seasonRace ->
                raceRemoteDataSource.getRaceResultsFlow(
                    seasonRace.season,
                    seasonRace.round
                )
            },
            SourceOfTruth.of(
                reader = { seasonRace ->
                    raceResultDao.getRaceResultsFull(seasonRace.season, seasonRace.round)
                        .complete({ it.driver.image }) {
                            driverDao.insert(getDriverWithImage(it))
                        }
                        .complete({ it.constructor.image }) {
                            constructorDao.insert(getConstructorWithImage(it))
                        }
                        .emptyListToNull()
                },
                writer = { _: SeasonRace, raceResultRemotes: List<RaceResultRemote> ->
                    driverDao.insertAll(RaceResultDriverMapper.toEntity(raceResultRemotes))
                    constructorDao.insertAll(RaceResultConstructorMapper.toEntity(raceResultRemotes))
                    raceResultDao.insertAll(RaceResultMapper.toEntity(raceResultRemotes))
                },
                deleteAll = {
                    withContext(Dispatchers.IO) {
                        driverDao.deleteAll()
                        constructorDao.deleteAll()
                        raceResultDao.deleteAll()
                    }
                }
            )
        ).build()


    private fun <T : List<U>, U> Flow<T>.complete(
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform {
            emit(it)
            it.firstOrNull { attr.invoke(it) == null }
                ?.let { action.invoke(it) }
        }.distinctUntilChanged()


    private suspend fun getCircuitWithFlag(raceFull: RaceFull) = raceFull.circuit.copy(
        location = raceFull.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                raceFull.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(raceResultFull: RaceResultFull) =
        raceResultFull.driver.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                raceResultFull.driver.url, 200, WikipediaService.Licence.FREE
            ) ?: "NONE"
        )

    private suspend fun getConstructorWithImage(result: RaceResultFull) =
        result.constructor.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                result.constructor.url, 200,
            ) ?: "NONE"
        )

    fun getAllRaces(season: Int): Flow<StoreResponse<List<RaceFull>>> {
        return raceStore.stream(StoreRequest.cached(season, false))
            .distinctUntilChangedBy { sr -> sr.dataOrNull() }
    }

    fun getRace(season: Int, round: Int): Flow<RaceFull> {
        return getAllRaces(season).transform {
            val first = it.requireData().first { it.race.round == round }
            emit(first)
        }
    }

    fun getRaceResults(
        season: Int,
        round: Int
    ): Flow<StoreResponse<List<RaceResultFull>>> =
        raceResultStore.stream(
            StoreRequest.cached(SeasonRace(season, round), false)
        )

    suspend fun refresh() {
        raceStore.clearAll()
        raceResultStore.clearAll()
    }

    data class SeasonRace(val season: Int, val round: Int)
}

