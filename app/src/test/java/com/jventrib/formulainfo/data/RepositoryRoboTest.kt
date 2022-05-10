package com.jventrib.formulainfo.data

import com.google.common.truth.Truth
import com.jventrib.formulainfo.data.remote.RoboTest
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class, sdk = [30])
class RepositoryRoboTest : RoboTest() {

    @Inject
    lateinit var raceRepository: RaceRepository

    @Test
    fun lastSeasonsRaces() {
        runBlocking {
            (2022 downTo 2020).asFlow().map {
                raceRepository.getRaces(it, false)
            }.flattenConcat().collect {
                Truth.assertThat(it).isNotEmpty()
            }
            (2022 downTo 2020).asFlow().map {
                raceRepository.getRaces(it, false)
            }.flattenConcat().collect {
                Truth.assertThat(it).isNotEmpty()
            }
        }
    }

    @Test
    fun season2021DriversStanding() {
        runBlocking {
            raceRepository.getSeasonStandings(2021, false)
                .drop(1)
                .collect {
                    Truth.assertThat(it).isNotEmpty()
                }
        }
    }

    @Test
    @Ignore(value = "Flow wont complete, so this test")
    fun allSeasonsRacesWithResults() {
        runBlocking {
            (2022 downTo 1950).asFlow().map {
                raceRepository.getRacesWithResults(it, false, false)
            }.flattenConcat().collect {
                Truth.assertThat(it).isNotEmpty()
            }
        }
    }
}
