package com.jventrib.formulainfo.race.data

import com.jventrib.formulainfo.race.data.db.*
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import java.time.Instant

@FlowPreview
@ExperimentalCoroutinesApi
class RaceRepositoryTest : TestCase() {
    private val testScope = TestCoroutineScope()

    @Test
    fun testGetAllRaces_whenDBIsEmpty() {
        val race = RaceFull(
            Race(
                2020,
                1,
                "http://test.com",
                "race1",
                "cir1",
                Race.Sessions(
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
        val circuitDao = mockk<CircuitDao>()
        val raceResultDao = mockk<RaceResultDao>()
        val driverDao = mockk<DriverDao>()
        val constructorDao = mockk<ConstructorDao>()
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()

        every { raceDao.getSeasonRaces(any()) } returns flowOf(listOf(race))
        coEvery { raceDao.insertAll(any()) } returns Unit
        coEvery { raceRemoteDataSource.getRacesFlow(any()) } returns flowOf(listOf(raceRemote))
        coEvery { raceRemoteDataSource.getCountryFlag(any()) } returns "flag1"

        val allRaces = RaceRepository(
            raceDao,
            circuitDao,
            raceResultDao,
            driverDao,
            constructorDao,
            raceRemoteDataSource
        ).getAllRaces(2020)


        testScope.launch {
            allRaces.collect {
                println(it.dataOrNull()?.get(0))
            }
        }
    }

}