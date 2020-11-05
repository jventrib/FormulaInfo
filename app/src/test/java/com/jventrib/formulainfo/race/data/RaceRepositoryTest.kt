package com.jventrib.formulainfo.race.data

import com.jventrib.formulainfo.race.data.db.ConstructorDao
import com.jventrib.formulainfo.race.data.db.DriverDao
import com.jventrib.formulainfo.race.data.db.RaceDao
import com.jventrib.formulainfo.race.data.db.RaceResultDao
import com.jventrib.formulainfo.race.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.race.model.Race
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

        val race = Race(
            2020,
            1,
            "http://test.com",
            "race1",
            Race.Circuit(
                "cir1",
                "http://circuit1.com",
                "Circuit 1 ",
                Race.Circuit.Location(
                    47.2197F,
                    14.7647F,
                    "Spielberg",
                    "Austria", null
                ),
                "http://image1.svg"
            ),
            Race.Sessions(Instant.now(), Instant.now(), Instant.now(), Instant.now(), Instant.now())
        )

        val raceDao = mockk<RaceDao>()
        val raceResultDao = mockk<RaceResultDao>()
        val driverDao = mockk<DriverDao>()
        val constructorDao = mockk<ConstructorDao>()
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()

        every { raceDao.getSeasonRaces(any()) } returns flowOf(listOf(race))
        coEvery { raceDao.insertAll(any()) } returns Unit
        coEvery { raceRemoteDataSource.getRaces(any()) } returns listOf(race)
        coEvery { raceRemoteDataSource.getCountryFlag(any()) } returns "flag1"

        val allRaces = RaceRepository(raceDao, raceResultDao, driverDao, constructorDao, raceRemoteDataSource).getAllRaces(2020)


        testScope.launch {
            allRaces.collect {
                println(it.dataOrNull()?.get(0))
            }
        }
    }

}