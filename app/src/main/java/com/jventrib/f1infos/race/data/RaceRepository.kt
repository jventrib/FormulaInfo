package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyFlowOfListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.data.db.RaceResultDao
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.RaceResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class RaceRepository(
    private val raceDao: RaceDao,
    private val raceResultDao: RaceResultDao,
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


    private val raceResultStore: Store<SeasonRace, List<RaceResult>> =
        StoreBuilder.from(
            Fetcher.ofFlow { seasonRace -> raceRemoteDataSource.getRaceResultsFlow(seasonRace.season, seasonRace.round) },
            SourceOfTruth.of(
                reader = { seasonRace ->
                    raceResultDao.getRaceResults(seasonRace.season, seasonRace.round).emptyFlowOfListToNull()
                },
                writer = { _: SeasonRace, raceResults: List<RaceResult> ->
                    raceResultDao.insertAll(raceResults)
                }
            )
        ).build()


    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        return raceStore.stream(StoreRequest.cached(2020, false))
//        return store.stream(StoreRequest.fresh(2020))
    }

    data class SeasonRace(val season: Int, val round: Int)

}

