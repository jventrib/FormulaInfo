package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyFlowOfListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class RaceRepository(
    private val raceDao: RaceDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {

    fun getAllRaces(scope: CoroutineScope): Flow<StoreResponse<List<Race>>> {
        val store = StoreBuilder.from(
            Fetcher.ofFlow { season: Int ->
//                Log.d(javaClass.name, "Get races from remoteDataSource")
                flow {
                    val races = raceRemoteDataSource.getRaces(season).onEach(Race::buildDatetime)
                    //First emit with all races, no flag loaded
                    emit(races)

                    //Then load the flags
                    races.forEach {
                        it.circuit.location.flag =
                            raceRemoteDataSource.getCountryFlag(it.circuit.location.country)
                        //Each time a flag is load, emit all the races
                        it.circuit.circuitImageUrl = raceRemoteDataSource.getCircuitImage(it.circuit.circuitUrl)
                        emit(races)
                    }
                }
            },
            SourceOfTruth.of(
                reader = { season ->
//                    Log.d(javaClass.name, "Get races from DB")
                    raceDao.getSeasonRaces(season).emptyFlowOfListToNull()
                },
                writer = { _: Int, races: List<Race> ->
                    raceDao.insertAll(races)
                }
            )
        )
            .scope(scope).build()
//        return store.stream(StoreRequest.cached(2020, false))
        return store.stream(StoreRequest.fresh(2020))
    }


}

