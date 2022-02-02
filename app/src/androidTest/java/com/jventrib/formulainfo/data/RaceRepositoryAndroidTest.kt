package com.jventrib.formulainfo.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
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
    fun testRepo() {
        runBlocking {
            val races = raceRepository.getRaceWithResultFlow(2021).collect {
                println("${it.result.resultInfo.key}-${it.result.driver.driverId}")
            }
        }
    }
}

