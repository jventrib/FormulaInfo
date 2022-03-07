package com.jventrib.formulainfo.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
            val lastEmit = races.last()
            lastEmit.forEach {
                assertThat(it.race.circuit.location.flag).isNotNull()
            }
        }
    }

    @Test
    fun allSeasonsRacesWithResults() {
        runBlocking {
            val l = (2022 downTo 1950).asFlow().map {
                println("season $it")
                raceRepository.getRacesWithResults(it, false, false)
            }.flattenConcat().last()
            assertThat(l).isNotEmpty()
        }
    }

    @Test
    @Ignore
    fun allSeasonsRacesWithResultsWithImages() {
        runBlocking {
            val l = (1950..2022).asFlow().map {
                // val l = (2022 downTo 1950).asFlow().map {
                raceRepository.getRacesWithResults(it, true, true)
            }.flattenMerge(200).collect()
        }
    }

    @Test
    @Ignore
    fun allDriversWithImages() {
        runBlocking {
            // val l = (2022 downTo 1950).asFlow().map {
            raceRepository.getAllDrivers().collect()
        }
    }

    @Test
    @Ignore
    fun allLaps() {
        runBlocking {
            val races = (1998 downTo 1994).asFlow().flatMapConcat { season ->
                raceRepository.getRaces(season, false).first().asFlow()
                    .map { season to it.raceInfo.round }
                // .flatMap { it.raceInfo.round }
            }

            val t = races
                .map {
                    delay(300)
                    raceRepository.getResultsWithLaps(it.first, it.second)
                }
                .flattenConcat()
                .collect()
            //         .flatMapConcat {
            //         it.map { race ->
            //         }.asFlow()
            //     }
            // }.flattenMerge(200).collect()
        }
    }

    @Test
    @Ignore
    fun refreshSeason() {
        runBlocking {
            raceRepository.refresh()
            delay(200_000)
        }
    }
}
