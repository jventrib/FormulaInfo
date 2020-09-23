package com.jventrib.f1infos.race.data

import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.Race
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.junit.Test

class RaceRepositoryTest : TestCase() {

    @Test
    fun testGetAllRaces_whenDBIsEmpty() {

        val raceDao = mockk<RaceDao>()
        every { raceDao.getSeasonRaces(any()) } returns flowOf(listOf())

        val raceRemoteDataSource = mockk<RaceRemoteDataSource>()
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
                    "Spielburg",
                    "Austria", null
                )
            )
        )
        coEvery { raceRemoteDataSource.getRaces(any()) } returns listOf(race)
        val allRaces = RaceRepository(raceDao, raceRemoteDataSource).getAllRaces()
        runBlocking {
            allRaces.collect {
                println(it.dataOrNull()?.get(0)?.raceName)
            }
        }
    }
}