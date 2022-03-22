package com.jventrib.formulainfo.data

import android.util.LruCache
import com.jventrib.formulainfo.data.db.LapDao
import com.jventrib.formulainfo.data.db.RaceDao
import com.jventrib.formulainfo.data.db.ResultDao
import com.jventrib.formulainfo.data.remote.MockRoomData
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.model.db.Circuit
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.RaceInfo
import com.jventrib.formulainfo.model.remote.RaceRemote
import com.jventrib.formulainfo.ui.race.getResultSample
import com.jventrib.formulainfo.utils.now
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class RaceRepositoryTest : TestCase() {
    private val testScope = TestScope()

    @Test
    fun testGetAllRaces_whenDBIsEmpty() {
        val race = Race(
            RaceInfo(
                2020,
                1,
                "http://test.com",
                "race1",
                "cir1",
                RaceInfo.Sessions(
                    now(),
                    now(),
                    now(),
                    now(),
                    now()
                )
            ),
            Circuit(
                "cir1",
                "http://circuit1.com",
                "Circuit 1 ",
                Circuit.Location(
                    47.2197F,
                    14.7647F,
                    "Spielberg",
                    "Austria", null
                ),
                "http://image1.svg"
            ),

        )

        val raceRemote = RaceRemote(
            2020,
            1,
            "http://test.com",
            "race1",
            RaceRemote.Circuit(
                "cir1",
                "http://circuit1.com",
                "Circuit 1 ",
                RaceRemote.Circuit.Location(
                    47.2197F,
                    14.7647F,
                    "Spielberg",
                    "Austria", null
                ),
                "http://image1.svg"
            ),
            RaceRemote.Sessions(
                now(),
                now(),
                now(),
                now(),
                now()
            )
        )
        val raceDao = mockk<RaceDao>()
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()

        every { raceDao.getRaces(any()) } returns flowOf(listOf(race))
        coEvery { raceDao.insertAll(any()) } returns Unit
        coEvery { raceRemoteDataSource.getRaces(any()) } returns listOf(raceRemote)
        coEvery { raceRemoteDataSource.getCountryFlag(any()) } returns "flag1"

//        val allRaces = RaceRepository(
//            AppRoomDatabase.getDatabase(context),
//            raceRemoteDataSource
//        ).getAllRaces(2020)
//
//
//        testScope.launch {
//            allRaces.collect {
//                println(it.dataOrNull()?.get(0))
//            }
//        }
    }

    @Test
    fun testGetRaceGraph() {
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()
        val resultDao = mockk<ResultDao>()
        every { resultDao.getResults(any(), any()) } returns flowOf(
            listOf(
                getResultSample("verstappen", 1),
                getResultSample("hamilton", 2),
                getResultSample("bottas", 3),
                getResultSample("norris", 4),
                getResultSample("leclerc", 5),
                getResultSample("sainz", 6),
                getResultSample("alonso", 7),
                getResultSample("gasly", 8),
                getResultSample("ocon", 9),
                getResultSample("perrez", 9),
            )
        )

        val lapTimeDao = mockk<LapDao>()
        val season = slot<Int>()
        val round = slot<Int>()
        val driverId = slot<String>()
        every { lapTimeDao.getAll(capture(season), capture(round), capture(driverId)) } returns
            flow {
                delay(2000)
                emit(

                    (1..10).map {
                        Lap(
                            season.captured,
                            round.captured,
                            driverId.captured,
                            driverId.captured,
                            it,
                            1,
                            65_000,
                            65_000
                        )
                    }

                )
            }

        val cacheMock = mockk<LruCache<List<Any>, Any>>()
        every { cacheMock.get(any()) }.returns(null)
        every { cacheMock.put(any(), any()) }.answers {}
        val resultGraph = RaceRepository(
            MockRoomData(resultDao = resultDao, lapDao = lapTimeDao),
            raceRemoteDataSource, context = mockk(),
            cache = cacheMock
        ).getResultsWithLaps(2021, 1)
        runBlocking {
            resultGraph.collect {
                println(it)
            }
        }
    }

    @Test
    fun testFlowListToMap() {
        val driversFlow = flowOf(listOf("a", "b", "c"))

        testScope.runTest {
            val map = mutableMapOf<String, List<Int>>()
            val transform = driversFlow
                .transform { drivers ->
                    drivers.forEach { driver ->
                        getLaps()
                            .collect {
                                println(it)
                                map[driver] = it
                                emit(map)
                            }
                    }
                }
            transform.collect {
                println(it)
            }
        }
    }

    private fun getLaps() =
        flowOf((1..10).toList())
}
