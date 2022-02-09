package com.jventrib.formulainfo.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RaceRepositoryAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @get:Rule
//    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
        AndroidLogcatLogger.installOnDebuggableApp(
            ApplicationProvider.getApplicationContext(),
            minPriority = LogPriority.VERBOSE
        )

    }

    @Inject
    lateinit var raceRepository: RaceRepository


    @Test
    fun refresh() {
        runBlocking {
            raceRepository.refresh()
        }
    }

    @Test
    fun testRaceResultsWithoutFlagsAndDriverImages() {
        runBlocking {
            raceRepository.refresh()
            val racesEmits =
                raceRepository.getRacesWithResults(2021, false, false)
                    .onEach { println(it) }
                    .toList()
            println(racesEmits.size)
            assertThat(racesEmits).hasSize(23)
            val lastEmit = racesEmits.last()
            lastEmit.forEach {
                assertThat(it.race.circuit.location.flag).isNull()
            }
        }
        runBlocking {
            // From Cache
            val racesEmits =
                raceRepository.getRacesWithResults(2021, false, false)
                    .onEach { println(it) }
                    .toList()
            println(racesEmits.size)
            assertThat(racesEmits).hasSize(24)
            val lastEmit = racesEmits.last()
            lastEmit.forEach {
                assertThat(it.race.circuit.location.flag).isNull()
            }
        }
    }

    @Test
    fun testRaceResultsWithFlagsAndWithoutDriverImages() {
        runBlocking {
            raceRepository.refresh()
            val races =
                raceRepository.getRacesWithResults(2021, false, true)
                    .onEach { println(it) }
                    .toList()
            println(races.size)
            assertThat(races).hasSize(42)
            val lastEmit = races.last()
            lastEmit.forEach {
                assertThat(it.race.circuit.location.flag).isNotNull()
            }
        }
        runBlocking {
            // From Cache
            val races =
                raceRepository.getRacesWithResults(2021, false, true)
                    .onEach { println(it) }
                    .toList()
            println(races.size)
            assertThat(races).hasSize(23)
            val lastEmit = races.last()
            lastEmit.forEach {
                assertThat(it.race.circuit.location.flag).isNotNull()
            }
        }
    }


    @Test
    fun allSeasonsRacesWithResults() {
        runBlocking {
            val t = (2022 downTo 1950).asFlow().map {
                raceRepository.getRacesWithResults(it, false, false)
            }.flattenConcat().collect {
                println(it)
                assertThat(it).isNotEmpty()
            }
        }
    }
}

