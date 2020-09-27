package com.jventrib.f1infos.race.data

import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.Race
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class RaceRepositoryTest : TestCase() {
    private val testScope = TestCoroutineScope()

    @Test
    fun testGetAllRaces_whenDBIsEmpty() {

        val race = Race(
            2020,
            1,
            "http://test.com",
            "race1",
            "2020-07-05",
            "13:10:00Z",
            null,
            Race.Circuit(
                "cir1",
                "http://circuit1.com",
                "Circuit 1 ",
                Race.Circuit.Location(
                    47.2197F,
                    14.7647F,
                    "Spielberg",
                    "Austria", null
                )
            )
        )

        val raceDao = mockk<RaceDao>()
        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()

        every { raceDao.getSeasonRaces(any()) } returns flowOf(listOf(race))
        coEvery { raceDao.insertAll(any()) } returns Unit
        coEvery { raceRemoteDataSource.getRaces(any()) } returns listOf(race)
        coEvery { raceRemoteDataSource.getCountryFlag(any()) } returns "flag1"

        val allRaces = RaceRepository(raceDao, raceRemoteDataSource).getAllRaces(testScope)


        val async = testScope.launch {
            allRaces.collect {
                println(it.dataOrNull()?.get(0))
            }
        }
    }

}