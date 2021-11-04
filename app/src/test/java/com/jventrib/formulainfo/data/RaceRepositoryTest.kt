package com.jventrib.formulainfo.data

import com.jventrib.formulainfo.data.db.*
import com.jventrib.formulainfo.data.remote.MockRoomData
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.model.db.*
import com.jventrib.formulainfo.model.remote.RaceRemote
import com.jventrib.formulainfo.result.getResultSample
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.time.Duration
import java.time.Instant

@FlowPreview
@ExperimentalCoroutinesApi
class RaceRepositoryTest : TestCase() {
    private val testScope = TestCoroutineScope()

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
                    Instant.now(),
                    Instant.now(),
                    Instant.now(),
                    Instant.now(),
                    Instant.now()
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
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now()
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
    fun testgetRaceGraph() {

        val roomDb = mockk<RaceRemoteDataSource>()
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()

        val result = getResultSample()
        val result2 = result.copy(driver = result.driver.copy(driverId = "will"))
        val resultDao = mockk<ResultDao>()
        every { resultDao.getResults(any(), any()) } returns flowOf(listOf(result, result2))

        val lapTimeDao = mockk<LapDao>()
        val laps = listOf(
            Lap("k", 2021, 1, "doe", 1, 1, Duration.ofSeconds(10)),
            Lap("k", 2021, 1, "doe", 2, 1, Duration.ofSeconds(10)),
            Lap("k", 2021, 1, "doe", 3, 1, Duration.ofSeconds(10)),
            Lap("k", 2021, 1, "doe", 4, 1, Duration.ofSeconds(10)),
        )
        every { lapTimeDao.getAll(any(), any(), any()) } returns flowOf(laps)

        val resultGraph = RaceRepository(
            MockRoomData(resultDao = resultDao, lapDao = lapTimeDao),
            raceRemoteDataSource, context = mockk()
        ).getResultGraph(2021, 1)
        testScope.runBlockingTest {
            resultGraph.collect {
                println(it)
            }
        }
    }


    @Test
    fun testFlowListToMap() {
        val driversFlow = flowOf(listOf("a", "b", "c"))

        testScope.runBlockingTest {
            val map = mutableMapOf<String, List<Int>>()
            val transform = driversFlow
                .transform { drivers ->
                    drivers.forEach { driver ->
                        getLaps(driver)
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


    private fun getLaps(s: String) =
        flowOf((1..10).toList())
}
