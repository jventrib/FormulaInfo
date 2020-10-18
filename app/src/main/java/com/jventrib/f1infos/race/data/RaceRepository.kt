package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyFlowOfListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class RaceRepository(
    private val raceDao: RaceDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {
    private val store: Store<Int, List<Race>>
    init {
        store = StoreBuilder.from(
            Fetcher.ofFlow { season -> raceRemoteDataSource.getRacesFlow(season) },
            SourceOfTruth.of(
                reader = { season ->
                    raceDao.getSeasonRaces(season).emptyFlowOfListToNull()
                },
                writer = { _: Int, races: List<Race> ->
                    raceDao.insertAll(races)
                }
            )
        )
//            .scope(scope)
            .build()

    }

    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        return store.stream(StoreRequest.cached(2020, false))
//        return store.stream(StoreRequest.fresh(2020))
    }


}

