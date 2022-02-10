package com.jventrib.formulainfo.data.remote

import com.google.common.truth.Truth
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.util.JULLogger
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import logcat.LogcatLogger
import logcat.logcat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.logging.Level
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class, sdk = [30])
class MrdServiceRoboTest : RoboTest() {

    @Inject
    lateinit var mrdService: MrdService

    @Test
    fun getRaces2021() {
        runBlocking {
            val races = mrdService.getSchedule(2021).mrData.table.races
            logcat { races.toString() }
            Truth.assertThat(races).isNotEmpty()
        }
    }

    @Test
    fun getRaces2033() {
        runBlocking {
            val races = mrdService.getSchedule(2033).mrData.table.races
            logcat { races.toString() }
            Truth.assertThat(races).isEmpty()
        }
    }
}