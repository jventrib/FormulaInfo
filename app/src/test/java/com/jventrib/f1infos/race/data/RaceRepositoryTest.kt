package com.jventrib.f1infos.race.data

import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.race.data.db.RaceDao
import com.jventrib.f1infos.race.data.remote.MockRaceRemoteDataSource
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.model.Race
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.junit.Test

class RaceRepositoryTest : TestCase() {

    @Test
    fun testGetAllRaces_whenDBIsEmpty() {

        val raceDao = mockk<RaceDao>()

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
                    "Spielberg",
                    "Austria", null
                )
            )
        )
        runBlocking {

            every { raceDao.getSeasonRaces(any()) } returns flowOf(listOf())
            coEvery { raceDao.insertAll(any()) } returns Unit
            coEvery { raceRemoteDataSource.getRaces(any()) } returns listOf(race)
            coEvery { raceRemoteDataSource.getCountryFlag(any()) } returns "flag1"
            val allRaces = RaceRepository(raceDao, raceRemoteDataSource).getAllRaces()
//            val allRaces = flowOf(StoreResponse.Data(listOf(race), ResponseOrigin.Fetcher))
//            val allRaces = flowOf<StoreResponse.Data<List<Race>>>(StoreResponse.Data(raceRemoteDataSource.getRaces(2020), ResponseOrigin.Fetcher))


            allRaces.take(3).collect {
                println(it.dataOrNull()?.get(0))
//                cancel()
            }
        }
    }
}