package com.jventrib.formulainfo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import androidx.room.withTransaction
import coil.imageLoader
import coil.request.ImageRequest
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.jventrib.formulainfo.data.db.*
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.data.remote.WikipediaService
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.model.db.FullRaceResult
import com.jventrib.formulainfo.model.mapper.*
import com.jventrib.formulainfo.utils.detect
import kotlinx.coroutines.flow.*
import logcat.LogPriority
import logcat.logcat
import java.time.Instant
import kotlin.math.hypot

class RaceRepository(
    private val roomDb: AppRoomDatabase,
    private val raceRemoteDataSource: RaceRemoteDataSource,
    private val context: Context
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
            }
            .onEach { response ->
                response.dataOrNull()?.forEach { it.nextRace = false }
                response.dataOrNull()?.first { it.race.sessions.race.isAfter(Instant.now()) }
                    ?.let { it.nextRace = true }

            }
            .onEach { logcat(LogPriority.VERBOSE) { "Response: $it" } }
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


    private suspend fun getCircuitWithFlag(fullRace: FullRace) = fullRace.circuit.copy(
        location = fullRace.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                fullRace.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(
        fullRaceResult: FullRaceResult,
    ): Driver {
        val imageUrl = raceRemoteDataSource.getWikipediaImageFromUrl(
            fullRaceResult.driver.url, 200, WikipediaService.Licence.FREE
        ) ?: "NONE"

        val drawable = context.imageLoader.execute(
            ImageRequest.Builder(context).data(imageUrl).build()
        ).drawable

        val bitmap =
            (drawable as? BitmapDrawable)?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        val centerRect = bitmap?.let { detect(it) }

        return fullRaceResult.driver.copy(
            image = imageUrl,
            faceBox = centerRect?.flattenToString()
        )
    }

    private suspend fun getConstructorWithImage(result: FullRaceResult) =
        result.constructor.copy(
            image = raceRemoteDataSource.getWikipediaImageFromUrl(
                result.constructor.url, 200,
            ) ?: "NONE"
        )

}

