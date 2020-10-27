package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyFlowOfListToNull
import com.jventrib.f1infos.race.data.db.DriverDao
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.data.db.RaceResultDao
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.db.RaceResultWithDriver
import com.jventrib.f1infos.race.model.mapper.RaceResultMapper
import com.jventrib.f1infos.race.model.remote.RaceResultRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class RaceRepository(
    private val raceDao: RaceDao,
    private val raceResultDao: RaceResultDao,
    private val driverDao: DriverDao,
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


    private val raceResultRemoteStore: Store<SeasonRace, List<RaceResultWithDriver>> =
        StoreBuilder.from(
            Fetcher.ofFlow { seasonRace ->
                raceRemoteDataSource.getRaceResultsFlow(
                    seasonRace.season,
                    seasonRace.round
                )
            },
            SourceOfTruth.of(
                reader = { seasonRace ->
                    raceResultDao.getRaceResultsWithDrivers(seasonRace.season, seasonRace.round)
                        .transform {
                            emit(it)
                            it
                                .firstOrNull() { raceResultWithDriver -> raceResultWithDriver.driver.image == null }
                                ?.let { raceResultWithDriver ->
                                    val copy = getRaceResultWithDriverImage(raceResultWithDriver)
                                    driverDao.insert(copy.driver)
                                }
                        }.emptyFlowOfListToNull()
                },
                writer = { _: SeasonRace, raceResultRemotes: List<RaceResultRemote> ->
                    driverDao.insertAll(RaceResultMapper.toDriverEntity(raceResultRemotes))
                    raceResultDao.insertAll(RaceResultMapper.toEntity(raceResultRemotes))
                }
            )
        ).build()

    private suspend fun getRaceResultWithDriverImage(raceResultWithDriver: RaceResultWithDriver): RaceResultWithDriver {
        val copy = raceResultWithDriver.copy(
            driver = raceResultWithDriver.driver.copy(
                image = raceRemoteDataSource.getWikipediaImageFromUrl(
                    raceResultWithDriver.driver.url,200
                ) ?: "NONE"
            )
        )
        return copy
    }


    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        return raceStore.stream(StoreRequest.cached(2020, false))
//        return raceStore.stream(StoreRequest.fresh(2020))
    }


    fun getRaceResults(season: Int, round: Int): Flow<StoreResponse<List<RaceResultWithDriver>>> =
        raceResultRemoteStore.stream(StoreRequest.cached(SeasonRace(season, round), false))
//        raceResultRemoteStore.stream(StoreRequest.fresh(SeasonRace(season, round)))

    data class SeasonRace(val season: Int, val round: Int)

}

