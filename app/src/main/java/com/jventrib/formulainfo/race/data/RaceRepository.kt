package com.jventrib.formulainfo.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.formulainfo.common.utils.emptyFlowOfListToNull
import com.jventrib.formulainfo.race.data.db.ConstructorDao
import com.jventrib.formulainfo.race.data.db.DriverDao
import com.jventrib.formulainfo.race.data.db.RaceDao
import com.jventrib.formulainfo.race.data.db.RaceResultDao
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.data.remote.WikipediaService
import com.jventrib.formulainfo.race.model.Race
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import com.jventrib.formulainfo.race.model.mapper.RaceResultMapper
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class RaceRepository(
    private val raceDao: RaceDao,
    private val raceResultDao: RaceResultDao,
    private val driverDao: DriverDao,
    private val constructorDao: ConstructorDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {
    private val raceStore: Store<Int, List<Race>> =
        StoreBuilder.from(
            Fetcher.ofFlow { season -> raceRemoteDataSource.getRacesFlow(season) },
            SourceOfTruth.of(
                reader = { season ->
                    raceDao.getSeasonRaces(season).emptyFlowOfListToNull()
                },
                writer = { _: Int, races: List<Race> ->
                    raceDao.insertAll(races)
                }
            )
        ).build()


    private val raceResultRemoteStoreAndConstructor: Store<SeasonRace, List<RaceResultFull>> =
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
                        .transform { list ->
                            emit(list)
                            list.firstOrNull { result -> result.driver.image == null }
                                ?.let { result ->
                                    val copy = getDriverWithImage(result)
                                    driverDao.insert(copy)
                                }
                            list.firstOrNull { result -> result.constructor.image == null }
                                ?.let { result ->
                                    val copy = getConstructorWithImage(result)
                                    constructorDao.insert(copy)
                                }
                        }.emptyFlowOfListToNull()
                },
                writer = { _: SeasonRace, raceResultRemotes: List<RaceResultRemote> ->
                    driverDao.insertAll(RaceResultMapper.toDriverEntity(raceResultRemotes))
                    constructorDao.insertAll(RaceResultMapper.toConstructorEntity(raceResultRemotes))
                    raceResultDao.insertAll(RaceResultMapper.toEntity(raceResultRemotes))
                }
            )
        ).build()

    private suspend fun getDriverWithImage(raceResultFull: RaceResultFull) =
        raceResultFull.driver.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                raceResultFull.driver.url, 200, WikipediaService.Licence.FREE
            ) ?: "NONE"
        )

    private suspend fun getConstructorWithImage(result: RaceResultFull) =
        result.constructor.copy(image = raceRemoteDataSource.getWikipediaImageFromUrl(
            result.constructor.url, 200,
        ) ?: "NONE")


    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        return raceStore.stream(StoreRequest.cached(2020, false))
//        return raceStore.stream(StoreRequest.fresh(2020))
    }


    fun getRaceResults(
        season: Int,
        round: Int
    ): Flow<StoreResponse<List<RaceResultFull>>> =
        raceResultRemoteStoreAndConstructor.stream(
            StoreRequest.cached(
                SeasonRace(season, round),
                false
            )
        )
//        raceResultRemoteStore.stream(StoreRequest.fresh(SeasonRace(season, round)))

    data class SeasonRace(val season: Int, val round: Int)

}

