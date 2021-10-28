package com.jventrib.formulainfo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.room.withTransaction
import coil.imageLoader
import coil.request.ImageRequest
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.db.*
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.data.remote.WikipediaService
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.mapper.*
import com.jventrib.formulainfo.utils.detect
import kotlinx.coroutines.flow.*
import logcat.LogPriority
import logcat.logcat
import java.time.Instant

class RaceRepository(
    private val roomDb: AppRoomDatabase,
    private val raceRemoteDataSource: RaceRemoteDataSource,
    private val context: Context
) {
    private val raceDao: RaceDao = roomDb.raceDao()
    private val circuitDao: CircuitDao = roomDb.circuitDao()
    private val resultDao: ResultDao = roomDb.resultDao()
    private val driverDao: DriverDao = roomDb.driverDao()
    private val constructorDao: ConstructorDao = roomDb.constructorDao()
    private val lapTimeDao: LapTimeDao = roomDb.lapTimeDao()

    fun getRaces(season: Int): Flow<StoreResponse<List<Race>>> =
        raceDao.getRaces(season)
            .distinctUntilChanged()
            .transformLatest { data ->
                emit(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                logcat { "Getting races from DB" }
                if (data.isEmpty()) {
                    logcat { "No races in DB, fetching from API" }
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
                    logcat { "Got ${data.size} races from DB" }
                    emit(StoreResponse.Data(data, ResponseOrigin.SourceOfTruth))
                }
            }
            .completeFlowMissing({ it.circuit.location.flag }) {
                logcat { "Completing circuit ${it.circuit.location.country} with image" }
                circuitDao.insert(getCircuitWithFlag(it))
            }
            .onEach { response ->
                response.dataOrNull()?.forEach { it.nextRace = false }
                response.dataOrNull()?.first { it.raceInfo.sessions.race.isAfter(Instant.now()) }
                    ?.let { it.nextRace = true }

            }
            .onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }

    fun getResults(
        season: Int,
        round: Int
    ): Flow<StoreResponse<List<Result>>> =
        resultDao.getResults(season, round)
            .distinctUntilChanged()
            .transformLatest { data ->
                emit(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                logcat { "Getting Results from DB" }
                if (data.isEmpty()) {
                    logcat { "No Results in DB, fetching from API" }
                    emit(StoreResponse.Loading(ResponseOrigin.Fetcher))
                    raceRemoteDataSource.getResults(season, round).also {
                        logcat { "Fetched Results from API: $it" }
                        roomDb.withTransaction {
                            driverDao.insertAll(ResultDriverMapper.toEntity(it)
                                .also { logcat { "Inserting Drivers $it" } })
                            constructorDao.insertAll(ResultConstructorMapper.toEntity(it)
                                .also { logcat { "Inserting Constructors $it" } })
                            resultDao.insertAll(ResultMapper.toEntity(season, round, it)
                                .also { logcat { "Inserting Results $it" } })
                        }
                    }
                } else {
                    logcat { "Got ${data.size} Results from DB" }
                    emit(StoreResponse.Data(data, ResponseOrigin.SourceOfTruth))
                }
            }
            .completeFlowMissing({ it.driver.image }) {
                logcat { "Completing driver ${it.driver.code} with image" }
                driverDao.insert(getDriverWithImage(it))
            }.onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }


    fun getLapTimes(
        season: Int,
        round: Int,
        driver: String
    ): Flow<StoreResponse<List<Lap>>> =
        lapTimeDao.getAll(season, round, driver)
            .distinctUntilChanged()
            .transformLatest { data ->
                emit(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                logcat { "Getting LapTime from DB" }
                if (data.isEmpty()) {
                    logcat { "No LapTime in DB, fetching from API" }
                    emit(StoreResponse.Loading(ResponseOrigin.Fetcher))
                    raceRemoteDataSource.getLapTime(season, round, driver).also {
                        logcat { "Fetched LapTime from API: $it" }
                            lapTimeDao.insertAll(LapTimeMapper.toEntity(season, round, driver, it)
                                .also { logcat { "Inserting LapTime $it" } })
                    }
                } else {
                    logcat { "Got ${data.size} LapTime from DB" }
                    emit(StoreResponse.Data(data, ResponseOrigin.SourceOfTruth))
                }
            }
            .onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }


    fun getRace(season: Int, round: Int): Flow<Race> {
        return raceDao.getRace(season, round)
            .distinctUntilChanged()
            .onEach {
                logcat { "GetRace $it" }
            }
            .filterNotNull()
            .flatMapLatest { completeRace(it) }
    }

    fun getResult(season: Int, round: Int, driverId: String): Flow<Result> =
        resultDao.getResult(season, round, driverId)

    suspend fun refresh() {
        roomDb.withTransaction {
            raceDao.deleteAll()
            circuitDao.deleteAll()
            resultDao.deleteAll()
            driverDao.deleteAll()
            constructorDao.deleteAll()
        }
    }

    private fun completeRace(r: Race) = flow {
        emit(r)
        if (r.circuit.imageUrl == null) {
            logcat { "Completing circuit ${r.circuit.id} with Image " }

            val circuitWithImage = r.circuit.copy(
                imageUrl = raceRemoteDataSource.getCircuitImage(r.circuit.url, 500)
            )
            circuitDao.insert(circuitWithImage)
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


    private suspend fun getCircuitWithFlag(race: Race) = race.circuit.copy(
        location = race.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                race.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(
        result: Result,
    ): Driver {
        val imageUrl = raceRemoteDataSource.getWikipediaImageFromUrl(
            result.driver.url, 200, WikipediaService.Licence.FREE
        ) ?: "NONE"

        val drawable = context.imageLoader.execute(
            ImageRequest.Builder(context).data(imageUrl).build()
        ).drawable

        val bitmap =
            (drawable as? BitmapDrawable)?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        val centerRect = bitmap?.let { detect(it) }

        return result.driver.copy(
            image = imageUrl,
            faceBox = centerRect?.flattenToString()
        )
    }

    private suspend fun getConstructorWithImage(result: Result) =
        result.constructor.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                result.constructor.url, 200,
            ) ?: "NONE"
        )

}

