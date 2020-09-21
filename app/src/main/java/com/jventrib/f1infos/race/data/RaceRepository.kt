package com.jventrib.f1infos.race.data

import android.util.Log
import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZonedDateTime

class RaceRepository(
    private val raceDao: RaceDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {

    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        val store = StoreBuilder.from(
            Fetcher.ofFlow { season: Int ->
                Log.d(javaClass.name, "Get races from remoteDataSource")
                flow<List<Race>> {
                    val races = raceRemoteDataSource.getRaces(season).onEach {
                        it.datetime = buildDatetime(it)
                    }
                    //First emit with all races, no flag loaded
                    emit(races)

                    races.forEach {
                        it.circuit.location.flag =
                            raceRemoteDataSource.getCountryFlag(it.circuit.location.country)
                        //Each time a flag is load, emit all the races
                        emit(races)
                    }
                }
            },
            SourceOfTruth.of(
                reader = { season ->
                    Log.d(javaClass.name, "Get races from DB")
                    raceDao.getSeasonRaces(season).map { it.emptyListToNull() }
                },
                writer = { _: Int, races: List<Race> ->
                    raceDao.insertAll(races)
                }
            )
        ).build()
        return store.stream(StoreRequest.cached(2020, false))
    }

    suspend fun insert(race: Race) {
        raceDao.insert(race)
    }

    private fun buildDatetime(r: Race): Instant =
        ZonedDateTime.parse("${r.date}T${r.time}").toInstant()


}

