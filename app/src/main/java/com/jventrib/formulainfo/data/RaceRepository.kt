package com.jventrib.formulainfo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.LruCache
import android.widget.Toast
import androidx.room.withTransaction
import coil.imageLoader
import coil.request.ImageRequest
import com.jventrib.formulainfo.data.db.AppRoomDatabase
import com.jventrib.formulainfo.data.db.CircuitDao
import com.jventrib.formulainfo.data.db.ConstructorDao
import com.jventrib.formulainfo.data.db.DriverDao
import com.jventrib.formulainfo.data.db.LapDao
import com.jventrib.formulainfo.data.db.RaceDao
import com.jventrib.formulainfo.data.db.ResultDao
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.data.remote.WikipediaService
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.aggregate.RaceWithResult
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.model.mapper.LapTimeMapper
import com.jventrib.formulainfo.model.mapper.RaceCircuitMapper
import com.jventrib.formulainfo.model.mapper.RaceMapper
import com.jventrib.formulainfo.model.mapper.ResultConstructorMapper
import com.jventrib.formulainfo.model.mapper.ResultDriverMapper
import com.jventrib.formulainfo.model.mapper.ResultMapper
import com.jventrib.formulainfo.model.remote.ResultRemote
import com.jventrib.formulainfo.utils.FaceDetection
import com.jventrib.formulainfo.utils.concat
import com.jventrib.formulainfo.utils.currentYear
import com.jventrib.formulainfo.utils.now
import com.jventrib.formulainfo.utils.testNow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.transformWhile
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.time.temporal.ChronoUnit

class RaceRepository(
    private val roomDb: AppRoomDatabase,
    private val raceRemoteDataSource: RaceRemoteDataSource,
    private val context: Context,
    private val cache: LruCache<List<Any>, Any> = LruCache<List<Any>, Any>(200)
) {
    private val raceDao: RaceDao = roomDb.raceDao()
    private val circuitDao: CircuitDao = roomDb.circuitDao()
    private val resultDao: ResultDao = roomDb.resultDao()
    private val driverDao: DriverDao = roomDb.driverDao()
    private val constructorDao: ConstructorDao = roomDb.constructorDao()
    private val lapDao: LapDao = roomDb.lapTimeDao()

    private inline fun <reified R : List<E>, reified E, reified S> repo(
        cacheKey: List<Any>,
        dbRead: () -> Flow<R>,
        crossinline remoteFetch: suspend () -> List<S>,
        crossinline dbInsert: suspend (l: List<S>) -> Unit,
    ): Flow<R> {
        val fromDb =
            withCache(cacheKey) {
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
            }
                .handleError {
                    this.emit(listOf<E>() as R)
                }

        return fromDb
    }

    private inline fun <reified T : List<E>, reified E> withCache(
        cacheKey: List<Any>,
        block: () -> Flow<T>
    ): Flow<T> {
        logcat { "Cache: $cache" }
        return cache[cacheKey]?.let {
            check(it is T) { it }
            logcat { "Got ${it.size} ${E::class.simpleName} from Cache" }
            flowOf(it)
        } ?: run {
            var last: T? = null
            val flow = block()
            val lastEmit = flow.onEach { last = it }
                .onCompletion {
                    if (last != null) {
                        logcat { "Putting in Cache" }
                        cache.put(cacheKey, last)
                    }
                }
            return lastEmit
        }
    }

    fun getRaces(season: Int, completeFlags: Boolean): Flow<List<Race>> =
        repo(
            cacheKey = listOf("races", season, completeFlags),
            dbRead = { raceDao.getRaces(season) },
            remoteFetch = { raceRemoteDataSource.getRaces(season) },
            dbInsert = {
                roomDb.withTransaction {
                    circuitDao.insertAll(
                        RaceCircuitMapper.toEntity(it)
                            .also { logcat { "Inserting Circuits" } }
                    )
                    raceDao.insertAll(
                        RaceMapper.toEntity(it)
                            .also { logcat { "Inserting Races" } }
                    )
                }
            }
        )
            .completeMissing(completeFlags, { it.circuit.location.flag }) {
                logcat { "Completing circuit ${it.raceInfo.raceName} with image" }
                circuitDao.insert(getCircuitWithFlag(it))
            }
            .onEach { response ->
                response.forEach { it.nextRace = false }
                response
                    .firstOrNull { it.raceInfo.sessions.race.isAfter(now()) }
                    ?.let { it.nextRace = true }
            }
            .mockDate()
            .transformWhile { list ->
                emit(list)
                list.isNotEmpty() && list.any { it.circuit.location.flag == null }
            }
            .let { if (completeFlags) it else it.take(1) }

    fun getQualResults(
        season: Int,
        round: Int,
        completeDriverImage: Boolean
    ): Flow<List<Result>> = getResults(
        season,
        round,
        Session.QUAL,
        completeDriverImage
    ) { s, r -> raceRemoteDataSource.getQualResults(s, r) }

    fun getSprintResults(
        season: Int,
        round: Int,
        completeDriverImage: Boolean
    ): Flow<List<Result>> = getResults(
        season,
        round,
        Session.SPRINT,
        completeDriverImage
    ) { s, r -> raceRemoteDataSource.getSprintResults(s, r) }

    fun getRaceResults(
        season: Int,
        round: Int,
        completeDriverImage: Boolean
    ): Flow<List<Result>> = getResults(
        season,
        round,
        Session.RACE,
        completeDriverImage
    ) { s, r -> raceRemoteDataSource.getResults(s, r) }

    private fun getResults(
        season: Int,
        round: Int,
        session: Session,
        completeDriverImage: Boolean,
        sessionResults: suspend RaceRemoteDataSource.(Int, Int) -> List<ResultRemote>
    ): Flow<List<Result>> {
        return repo(
            cacheKey = listOf("results", season, round, session, completeDriverImage),
            dbRead = { resultDao.getResults(season, round, session) },
            remoteFetch = { raceRemoteDataSource.sessionResults(season, round) },
            dbInsert = {
                roomDb.withTransaction {
                    driverDao.insertAll(
                        ResultDriverMapper.toEntity(it)
                            .also { logcat { "Inserting Drivers" } }
                    )
                    constructorDao.insertAll(
                        ResultConstructorMapper.toEntity(it)
                            .also { logcat { "Inserting Constructors" } }
                    )
                    resultDao.insertAll(
                        ResultMapper.toEntity(season, round, session, it)
                            .also { logcat { "Inserting Results" } }
                    )
                }
            }
        )
            .completeMissing(completeDriverImage, { it.driver.image }) {
                logcat { "Completing driver ${it.driver.driverId} with image" }
                driverDao.insert(getDriverWithImage(it.driver))
            }
            .onEach { response ->
                if (response.all { it.driver.image != null }) {
                    FaceDetection.close()
                }
            }
            .transformWhile { list ->
                val realData = list.size != 1 || list.first().resultInfo.number != -1
                if (realData) {
                    emit(list)
                }
                realData && list.any { it.driver.image == null }
            }
            .let { if (completeDriverImage) it else it.take(1) }
            .onEach { logcat { "getResult for session ${session.name} " } }
    }

    private fun getSeasonDrivers(season: Int): Flow<List<Driver>> =
        driverDao.getSeasonDrivers(season).completeMissing(true, { it.image }) {
            logcat { "Completing driver ${it.driverId} with image" }
            driverDao.insert(getDriverWithImage(it))
        }.onEach { response ->
            if (response.all { it.image != null }) {
                FaceDetection.close()
            }
        }

    fun getLaps(
        season: Int,
        round: Int,
        driver: Driver
    ): Flow<List<Lap>> =
        repo(
            cacheKey = listOf("laps", season, round, driver),
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
                                0,
                                0
                            )
                        )
                    }
                }
                lapDao.insertAll(list.also { logcat { "Inserting LapTime" } })
            }
        )
            .map { list -> list.filter { it.number >= 0 } }

    fun getResultsWithLaps(
        season: Int,
        round: Int,
    ) = getRaceResults(season, round, false)
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
            .onEach { logcat { "GetRace $it" } }
            .filterNotNull()
            .flatMapLatest { completeRace(it) }
            .handleError()
    }

    fun getResult(season: Int, round: Int, driverId: String): Flow<Result> =
        resultDao.getResult(season, round, driverId)

    suspend fun refresh() {
        logcat { "Refreshing" }
        roomDb.withTransaction {
            val season = currentYear()
            raceDao.deleteSeason(season)
            // raceDao.deleteSeason(season - 1)
            resultDao.deleteSeason(season)
            // resultDao.deleteSeason(season - 1)
            lapDao.deleteSeason(season)
            // circuitDao.deleteAll()
            // driverDao.deleteAll()
            // constructorDao.deleteAll()
        }
        cache.evictAll()
        logcat { "Refresh done" }
    }

    suspend fun refreshPreviousRaces(round: Int) {
        logcat { "Refreshing previous races" }
        roomDb.withTransaction {
            val season = currentYear()
            resultDao.deleteCurrentSeasonPastRaces(season, round)
        }
        cache.evictAll()
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
        }.handleError()

    private suspend fun getCircuitWithFlag(race: Race) = race.circuit.copy(
        location = race.circuit.location.copy(
            flag = raceRemoteDataSource.getCountryFlag(
                race.circuit.location.country
            )
        )
    )

    private suspend fun getDriverWithImage(
        driver: Driver,
    ): Driver {
        val imageUrl = raceRemoteDataSource.getWikipediaImageFromUrl(
            driver.url, 200, WikipediaService.Licence.FREE
        ) ?: "NONE"
        logcat(LogPriority.INFO) { "imageUrl: $imageUrl" }

        val drawable = context.imageLoader.execute(
            ImageRequest.Builder(context).data(imageUrl).build()
        ).drawable

        val bitmap =
            (drawable as? BitmapDrawable)?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        val centerRect = bitmap?.let { FaceDetection.detect(it) }

        return driver.copy(
            image = imageUrl,
            faceBox = centerRect?.flattenToString()
        )
    }

//    private suspend fun getConstructorWithImage(result: Result) =
//        result.constructor.copy(
//            image = raceRemoteDataSource.getWikipediaImageFromUrl(
//                result.constructor.url, 200,
//            ) ?: "NONE"
//        )

    fun getRacesWithResults(
        season: Int,
        withDriverImages: Boolean,
        withFlags: Boolean,
    ): Flow<List<RaceWithResults>> {
        val racesWithFlags = getRaces(season, withFlags).map { list ->
            list.map { RaceWithResults(it, listOf()) }
        }

        val withResults = getRaces(season, withFlags)
            .take(1)
            .onEach { list ->
                val prevRace = (
                    list.firstOrNull { it.nextRace }?.raceInfo?.round?.minus(1)
                        ?: list.lastOrNull()?.raceInfo?.round
                    )
                prevRace?.let { refreshPreviousRaces(it) }
            }
            .flatMapLatest { it.asFlow() }
            .flatMapMerge(100) { race ->
                getRaceAndSprintResults(season, race, false)
            }
            .scan(mapOf<Int, RaceWithResults>()) { acc, value ->
                acc + (value.race.raceInfo.round to value)
            }
            .map { it.values.toList() }

        val racesWithResults = flowOf(listOf<RaceWithResults>()).concat(withResults)

        val racesWithResultsAndDriverImages =
            racesWithResults.combine(getSeasonDrivers(season)) { a, b ->
                if (b.isEmpty()) a
                else
                    a.map { raceWithResults ->
                        raceWithResults.copy(
                            results = raceWithResults.results.map { result ->
                                val map = b.associateBy { it.driverId }
                                val driver = map[result.driver.driverId]
                                driver?.let { result.copy(driver = it) } ?: result
                            }
                        )
                    }
            }

        val results = if (withDriverImages)
            racesWithResultsAndDriverImages else racesWithResults

        return racesWithFlags.combine(results) { left, right ->
            if (right.isEmpty()) {
                left
            } else {
                (
                    left.associateBy { it.race.raceInfo.round } +
                        right.associateBy { it.race.raceInfo.round }
                    ).values.toList()
                    .zip(left) { a, b -> a.copy(race = b.race) }
            }
        }
    }

    private fun getRaceAndSprintResults(
        season: Int,
        race: Race,
        completeDriverImage: Boolean
    ): Flow<RaceWithResults> {
        val sprintResultsFlow = flowOf(listOf<Result>())
            .run {
                if (race.raceInfo.sessions.sprint != null)
                    concat(getSprintResults(season, race.raceInfo.round, completeDriverImage))
                else this
            }
            .map { it.associateBy { it.resultInfo.driverId } }
        return getRaceResults(season, race.raceInfo.round, completeDriverImage)
            .combine(sprintResultsFlow) { raceResults, sprintResults ->
                logcat { "race: ${raceResults.size},  sprint: ${sprintResults.size}" }
                if (sprintResults.isEmpty()) {
                    raceResults
                } else {
                    raceResults.map { rl -> rl + sprintResults[rl.resultInfo.driverId] }
                }
            }
            .map {
                RaceWithResults(race, it)
            }
    }

    fun getRoundStandings(season: Int, round: Int?): Flow<List<DriverStanding>> {
        return getSeasonStandings(season, true)
            .map { map ->
                map.values.map { list -> round?.let { list.getOrNull(it) } ?: list.last() }
            }
            .map { list ->
                list.sortedByDescending { it.points }.mapIndexed { index, driverStanding ->
                    driverStanding.copy(position = index + 1)
                }
            }
            .debounce(20)
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

    fun getAllDrivers(): Flow<List<Driver>> {
        return driverDao.getAllDrivers()
            .completeMissing(true, { it.image }) {
                logcat { "Completing driver ${it.driverId} with image" }
                driverDao.insert(getDriverWithImage(it))
            }
    }

    private fun <T> Flow<T>.handleError(block: suspend FlowCollector<T>.(Throwable) -> Unit = {}): Flow<T> =
        this.catch { e ->
            logcat(priority = LogPriority.ERROR) { "Error: ${e.asLog()}" }
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            block(e)
        }

    private fun Flow<List<Race>>.mockDate(): Flow<List<Race>> = this.map { races ->
        var offset = 0L
        val minRace = 3
        races.map { race ->
            if (race.raceInfo.round >= minRace)
                race.copy(
                    raceInfo = race.raceInfo.copy(
                        sessions = race.raceInfo.sessions.copy(
                            fp1 = testNow.plus(120, ChronoUnit.MINUTES)
                                .plusSeconds(offset + 30),
                            fp2 = testNow.plus(120, ChronoUnit.MINUTES)
                                .plusSeconds(offset + 60),
                            fp3 = testNow.plus(120, ChronoUnit.MINUTES)
                                .plusSeconds(offset + 90),
                            qualifying = testNow.plus(120, ChronoUnit.MINUTES)
                                .plusSeconds(offset + 120),
                            race = testNow.plus(120, ChronoUnit.MINUTES)
                                .plusSeconds(offset + 150)
                        )
                    )
                ).apply {
                    nextRace = true
                    offset += 150
                }
            else race
        }
            .filter { it.raceInfo.round >= minRace }
    }

    private operator fun Result.plus(result: Result?): Result = this.copy(
        resultInfo = this.resultInfo
            .copy(
                points = this.resultInfo.points
                    .let { r -> (result?.let { r.plus((it.resultInfo.points)) }) ?: r }
            )
    )
}
