package com.jventrib.f1infos.race.data

import android.util.Log
import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZonedDateTime

class RaceRepository(
    private val raceDao: RaceDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {

    fun getAllRaces(): Flow<StoreResponse<List<Race>>> {
        val fetcher = Fetcher.of { season: Int ->
            Log.d(javaClass.name, "Get races from remoteDataSource")
            raceRemoteDataSource.getRaces(season).map { r ->
                r.apply {
                    datetime = buildDatetime(r)
                    circuit.location.flag =
                        raceRemoteDataSource.getCountryFlag(r.circuit.location.country)
                }
            }
        }
        val store = StoreBuilder.from(
            fetcher,
            SourceOfTruth.of(
                reader = { season ->
                    Log.d(javaClass.name, "Get races from DB")
                    raceDao.getSeasonRaces(season).emptyListToNull()
                },
                writer = { _: Int, races: List<Race> -> raceDao.insertAll(races) }
            )
        ).build()
        return store.stream(StoreRequest.fresh(2020))
    }

/*
        performGetOperation({ raceDao.getAllRaces() },
            { raceRemoteDataSource.getRaces() },
            {
                raceDao.insertAll(it.mrData.table.races.map { r ->
                    r.apply {
                        datetime = buildDatetime(r)
                        circuit.location.flag = raceRemoteDataSource.getCountryFlag(r.circuit.location.country)
                    }
                })
            })
*/


    suspend fun insert(race: Race) {
        raceDao.insert(race)
    }

    private fun buildDatetime(r: Race): Instant =
        ZonedDateTime.parse("${r.date}T${r.time}").toInstant()


}

