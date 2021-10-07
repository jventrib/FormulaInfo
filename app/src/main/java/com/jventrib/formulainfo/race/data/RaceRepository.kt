package com.jventrib.formulainfo.race.data

import androidx.room.withTransaction
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.db.*
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.data.remote.WikipediaService
import com.jventrib.formulainfo.race.model.db.FullRace
import com.jventrib.formulainfo.race.model.db.FullRaceResult
import com.jventrib.formulainfo.race.model.mapper.*
import kotlinx.coroutines.flow.*
import logcat.LogPriority
import logcat.logcat

class RaceRepository(
    val roomDb: AppRoomDatabase,
    private val raceRemoteDataSource: RaceRemoteDataSource
) {
    private val raceDao: RaceDao = roomDb.raceDao()
    private val circuitDao: CircuitDao = roomDb.circuitDao()
    private val raceResultDao: RaceResultDao = roomDb.raceResultDao()
    private val driverDao: DriverDao = roomDb.driverDao()
    private val constructorDao: ConstructorDao = roomDb.constructorDao()

    fun getRaces(season: Int): Flow<StoreResponse<List<FullRace>>> =
        raceDao.getRaces(season)
            .distinctUntilChanged()
            .transformLatest { data ->
                emit(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                logcat { "Getting FullRaces from DB" }
                if (data.isEmpty()) {
                    logcat { "No FullRaces in DB, fetching from API" }
                    emit(StoreResponse.Loading(ResponseOrigin.Fetcher))
                    raceRemoteDataSource.getRaces(season).also {
                        logcat { "Fetched Races from API: $it" }
                        roomDb.withTransaction {
                            circuitDao.insertAll(RaceCircuitMapper.toEntity(it)
                                .also { logcat { "Inserting Circuits $it" } })
                            raceDao.insertAll(RaceMapper.toEntity(it)
                                .also { logcat { "Inserting Races $it" } })
                        }
                    }
                } else {
                    logcat { "Got ${data.size} FullRaces from DB" }
                    emit(StoreResponse.Data(data, ResponseOrigin.SourceOfTruth))
                }
            }
            .completeFlowMissing({ it.circuit.location.flag }) {
                logcat { "Completing circuit ${it.circuit.location.country} with image" }
                circuitDao.insert(getCircuitWithFlag(it))
            }.onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }
//
//
//        raceStore.stream(StoreRequest.cached(season, false)).onEach {
//            logcat(LogPriority.VERBOSE) { "Races Response: $it" }
//        }

    fun getRaceResults(
        season: Int,
        round: Int
    ): Flow<StoreResponse<List<FullRaceResult>>> =
        raceResultDao.getFullRaceResults(season, round)
            .distinctUntilChanged()
            .transform { data ->
                emit(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                logcat { "Getting FullRaceResults from DB" }
                if (data.isEmpty()) {
                    logcat { "No FullRaceResults in DB, fetching from API" }
                    emit(StoreResponse.Loading(ResponseOrigin.Fetcher))
                    raceRemoteDataSource.getRaceResults(season, round).also {
                        logcat { "Fetched RaceResults from API: $it" }
                        roomDb.withTransaction {
                            driverDao.insertAll(RaceResultDriverMapper.toEntity(it)
                                .also { logcat { "Inserting Drivers $it" } })
                            constructorDao.insertAll(RaceResultConstructorMapper.toEntity(it)
                                .also { logcat { "Inserting Constructors $it" } })
                            raceResultDao.insertAll(RaceResultMapper.toEntity(season, round, it)
                                .also { logcat { "Inserting Results $it" } })
                        }
                    }
                } else {
                    logcat { "Got ${data.size} FullRaceResults from DB" }
                    emit(StoreResponse.Data(data, ResponseOrigin.SourceOfTruth))
                }
            }
            .completeFlowMissing({ it.driver.image }) {
                logcat { "Completing driver ${it.driver.code} with image" }
                driverDao.insert(getDriverWithImage(it))
            }.onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }


    fun getRace(season: Int, round: Int): Flow<FullRace> {
        return raceDao.getRace(season, round)
            .distinctUntilChanged()
            .onEach {
                logcat { "GetRace $it" }
            }
            .filterNotNull()
            .flatMapLatest { completeRace(it) }
    }

    suspend fun refresh() {
        roomDb.withTransaction {
            raceDao.deleteAll()
            circuitDao.deleteAll()
            raceResultDao.deleteAll()
            driverDao.deleteAll()
            constructorDao.deleteAll()
        }
    }

    private fun completeRace(r: FullRace) = flow {
        emit(r)
        if (r.circuit.imageUrl == null) {
            val circuitWithImage = r.circuit.copy(
                imageUrl = raceRemoteDataSource.getCircuitImage(r.circuit.url, 500)
            )
            circuitDao.insert(circuitWithImage)
            emit(r.copy(circuit = circuitWithImage))
        }
    }

    private fun <T : List<U>, U> Flow<T>.completeMissing(
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform { response ->
            emit(response)
            response.firstOrNull { attr(it) == null }?.let { action(it) }
        }

    private fun <T : StoreResponse<List<U>>, U> Flow<T>.completeFlowMissing(
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform { response ->
            emit(response)
            if (response is StoreResponse.Data<*>) {
                response.dataOrNull()?.firstOrNull { attr(it) == null }?.let { action(it) }
            }
        }


    private suspend fun getCircuitWithFlag(fullRace: FullRace) = fullRace.circuit.copy(
        location = fullRace.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                fullRace.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(fullRaceResult: FullRaceResult) =
        fullRaceResult.driver.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                fullRaceResult.driver.url, 200, WikipediaService.Licence.FREE
            ) ?: "NONE"
        )

    private suspend fun getConstructorWithImage(result: FullRaceResult) =
        result.constructor.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                result.constructor.url, 200,
            ) ?: "NONE"
        )

}

