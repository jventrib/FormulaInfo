package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.*
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
        val store = StoreBuilder.from(
            Fetcher.of { it: Int ->
                raceRemoteDataSource.getRaces().data!!.mrData.table.races.map { r ->
                    r.apply {
                        datetime = buildDatetime(r)
                        circuit.location.flag =
                            raceRemoteDataSource.getCountryFlag(r.circuit.location.country)
                    }
                }
            },
            SourceOfTruth.of(
                reader = { year -> raceDao.getAllRaces() },
                writer = { year: Int, races: List<Race> -> raceDao.insertAll(races) }
            )
        ).build()
        return store.stream(StoreRequest.cached(2020, refresh = true))
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

