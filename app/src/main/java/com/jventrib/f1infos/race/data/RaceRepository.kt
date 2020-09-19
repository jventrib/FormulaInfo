package com.jventrib.f1infos.race.data

import android.util.Log
import com.dropbox.android.external.store4.*
import com.jventrib.f1infos.common.utils.emptyListToNull
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.SeasonRace
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZonedDateTime

class RaceRepository(
    private val raceDao: RaceDao,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {

    fun getAllRaces(): Flow<StoreResponse<Race>> {
        val fetcher = Fetcher.ofFlow { season: SeasonRace ->
            Log.d(javaClass.name, "Get races from remoteDataSource")
            flow {
                raceRemoteDataSource.getRaces(season.season).forEach { r ->
                    r.apply {
                        datetime = buildDatetime(r)
                        circuit.location.flag =
                            raceRemoteDataSource.getCountryFlag(r.circuit.location.country)
                    }
                    emit(r)
                }
            }
        }
        val store = StoreBuilder.from(
            fetcher,
            SourceOfTruth.of(
                reader = { seasonRace ->
                    Log.d(javaClass.name, "Get races from DB")
                    val emptyListToNull =
                        raceDao.getSeasonRaces(seasonRace.season).flatMapLatest {
                            it.emptyListToNull()?.asFlow() ?: flowOf(null)
                        }
                    emptyListToNull
                },
                writer = { _: SeasonRace, race: Race ->
                    raceDao.insert(race)
                }
            )
        ).build()
        return store.stream(StoreRequest.fresh(SeasonRace(2020)))
    }

    suspend fun insert(race: Race) {
        raceDao.insert(race)
    }

    private fun buildDatetime(r: Race): Instant =
        ZonedDateTime.parse("${r.date}T${r.time}").toInstant()


}

