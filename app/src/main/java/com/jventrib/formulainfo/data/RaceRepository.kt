package com.jventrib.formulainfo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.room.withTransaction
import coil.imageLoader
import coil.request.ImageRequest
import com.jventrib.formulainfo.data.db.*
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.data.remote.WikipediaService
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.aggregate.RaceWithResult
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.mapper.*
import com.jventrib.formulainfo.utils.FaceDetection
import kotlinx.coroutines.flow.*
import logcat.LogPriority
import logcat.logcat
import java.time.Duration
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
    private val lapDao: LapDao = roomDb.lapTimeDao()

    private inline fun <R : List<E>, reified E, reified S> repo(
        dbRead: () -> Flow<R>,
        crossinline remoteFetch: suspend () -> List<S>,
        crossinline dbInsert: suspend (l: List<S>) -> Unit
    ): Flow<R> =
        dbRead.invoke()
            .distinctUntilChanged()
            .transformLatest { data ->
//                emit(StoreResponse.Loading(SourceOfTruth))
                logcat { "Getting ${E::class.simpleName} from DB" }
                if (data.isEmpty()) {
                    logcat { "No ${E::class.simpleName} in DB, fetching from remote" }
                    remoteFetch.invoke().also {
                        logcat { "Fetched ${S::class.simpleName} from remote" }
                        logcat(priority = LogPriority.VERBOSE) { "data: $it" }
                        dbInsert(it)
                        logcat { "Insert in DB done" }
                    }

                } else {
                    logcat { "Got ${data.size} ${E::class.simpleName} from DB" }
                    logcat(priority = LogPriority.VERBOSE) { "data: $data" }
                    this.emit(data)
                }
            }


    fun getRaces(season: Int, completeFlags: Boolean): Flow<List<Race>> =
        repo(
            dbRead = { raceDao.getRaces(season) },
            remoteFetch = { raceRemoteDataSource.getRaces(season) },
            dbInsert = {
                roomDb.withTransaction {
                    circuitDao.insertAll(RaceCircuitMapper.toEntity(it)
                        .also { logcat { "Inserting Circuits" } })
                    raceDao.insertAll(RaceMapper.toEntity(it)
                        .also { logcat { "Inserting Races" } })
                }
            })
            .completeMissing(completeFlags, { it.circuit.location.flag }) {
                logcat { "Completing circuit ${it.raceInfo.raceName} with image" }
                circuitDao.insert(getCircuitWithFlag(it))
            }
            .onEach { response ->
                response.forEach { it.nextRace = false }
                response
                    .firstOrNull { it.raceInfo.sessions.race.isAfter(Instant.now()) }
                    ?.let { it.nextRace = true }

            }
            .transformWhile {
                emit(it)
                it.isNotEmpty() && it.any { it.circuit.location.flag == null }
            }


    fun getResults(
        season: Int,
        round: Int,
        completeDriverImage: Boolean
    ): Flow<List<Result>> =
        repo(
            dbRead = { resultDao.getResults(season, round) },
            remoteFetch = { raceRemoteDataSource.getResults(season, round) },
            dbInsert = {
                roomDb.withTransaction {
                    driverDao.insertAll(ResultDriverMapper.toEntity(it)
                        .also { logcat { "Inserting Drivers" } })
                    constructorDao.insertAll(ResultConstructorMapper.toEntity(it)
                        .also { logcat { "Inserting Constructors" } })
                    resultDao.insertAll(ResultMapper.toEntity(season, round, it)
                        .also { logcat { "Inserting Results" } })
                }
            })
//            .filter {
//                it.size != 1 || it.first().resultInfo.number != -1
//            }
            .completeMissing(completeDriverImage, { it.driver.image })
            {
                logcat { "Completing driver ${it.driver.code} with image" }
                driverDao.insert(getDriverWithImage(it))
            }
            .onEach { response ->
                if (response.all { it.driver.image != null }) {
                    FaceDetection.close()
                }
            }
            .transformWhile {
                val realData = it.size != 1 || it.first().resultInfo.number != -1
                if (realData) {
                    emit(it)
                }
                realData && it.any { it.driver.image == null }
            }
            .let { if (completeDriverImage) it else it.take(1) }


    fun getLaps(
        season: Int,
        round: Int,
        driver: Driver
    ): Flow<List<Lap>> =
        repo(
            dbRead = { lapDao.getAll(season, round, driver.driverId) },
            remoteFetch = { raceRemoteDataSource.getLapTime(season, round, driver.driverId) },
            dbInsert = {
                val list = LapTimeMapper.toEntity(
                    season,
                    round,
                    driver.driverId,
                    driver.code ?: driver.driverId,
                    it
                ).run {
                    ifEmpty {
                        listOf(
                            Lap(
                                season,
                                round,
                                driver.driverId,
                                driver.code ?: driver.driverId,
                                -1,
                                -1,
                                Duration.ZERO,
                                Duration.ZERO
                            )
                        )
                    }
                }
                lapDao.insertAll(list.also { logcat { "Inserting LapTime" } })
            })
            .map { list -> list.filter { it.number >= 0 } }


    fun getResultsWithLaps(
        season: Int,
        round: Int,
    ) = getResults(season, round, false)
        .take(1)
        .flatMapLatest { it.asFlow() }
        .flatMapMerge(20) { result ->
            getLaps(season, round, result.driver)
                .take(1)
                .map { result to it }
        }
        .onEach { println("Pair: ${it.first} -> ${it.second.size}") }
        .runningFold(mapOf<Result, List<Lap>>()) { acc, value -> acc + value }


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
        logcat { "Refreshing" }
        roomDb.withTransaction {
            raceDao.deleteAll()
            circuitDao.deleteAll()
            resultDao.deleteAll()
            driverDao.deleteAll()
            constructorDao.deleteAll()
            lapDao.deleteAll()
        }
        logcat { "Refresh done" }
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
        complete: Boolean,
        attr: (U) -> Any?,
        action: suspend (U) -> Unit
    ): Flow<T> =
        this.transform { response ->
            emit(response)
            if (complete) {
                response.firstOrNull { attr(it) == null }?.let {
                    action(it)
                }
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
        val centerRect = bitmap?.let { FaceDetection.detect(it) }

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

    fun getRacesWithResults(
        season: Int,
        completeDriverImages: Boolean,
        completeFlags: Boolean,
    ): Flow<List<RaceWithResults>> {
        val racesWithFlags = getRaces(season, completeFlags).map {
            it.map { RaceWithResults(it, listOf()) }
        }

        val f1b = getRaces(season, completeFlags)
            .take(1)
            .map {
                it.map { RaceWithResults(it, listOf()) }
            }

        val withResults = getRaces(season, completeFlags)
            .take(1)
            .flatMapLatest { it.asFlow() }
            .flatMapMerge(100) { race ->
                getResults(season, race.raceInfo.round, completeDriverImages).map {
                    RaceWithResults(race, it)
                }
            }
            .scan(mapOf<Int, RaceWithResults>()) { acc, value ->
                acc + (value.race.raceInfo.round to value)
            }.map { it.values.toList() }

        val racesWithResults = f1b.onCompletion {
            emitAll(withResults)
        }
        return racesWithFlags.combine(racesWithResults) { withFlags, withResults ->
            (withFlags.associateBy { it.race.raceInfo.round }
                    + withResults.associateBy { it.race.raceInfo.round }).values.toList()
                .zip(withFlags) { a, b ->
                    a.copy(race = b.race)
                }
        }
    }

    fun getRoundStandings(season: Int, round: Int?): Flow<List<DriverStanding>> {
        return getSeasonStandings(season, true)
            .map { map ->
                map.values.map { list -> round?.let { list.getOrNull(it) } ?: list.last() }
            }
            .map {
                it.sortedByDescending { it.points }.mapIndexed { index, driverStanding ->
                    driverStanding.copy(position = index + 1)
                }
            }
    }

    fun getSeasonStandings(
        season: Int,
        completeDriverImages: Boolean
    ): Flow<Map<Driver, List<DriverStanding>>> =
        getRacesWithResults(season, completeDriverImages, false)
            .map { data ->
                data.flatMap { rrs -> rrs.results.map { RaceWithResult(rrs.race, it) } }
            }
            .map { list ->
                list.groupBy { it.result.driver }
                    .mapValues { entry ->
                        entry.value
                            .sortedBy { it.race.raceInfo.round }
                            .scan(
                                DriverStanding(
                                    entry.key,
                                    entry.value.first().result.constructor,
                                    0f,
                                    0,
                                    0
                                )
                            ) { acc, rr ->
                                acc.copy(
                                    points = acc.points + rr.result.resultInfo.points,
                                    round = rr.race.raceInfo.round
                                )
                            }
                    }
            }
}

