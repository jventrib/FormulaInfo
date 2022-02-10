package com.jventrib.formulainfo.data.remote

import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class, sdk = [30])
class RaceRemoteDataSourceRoboTest : RoboTest() {

    @Inject
    lateinit var raceRemoteDataSource: RaceRemoteDataSource

    @Test
    fun getRaces2033() {
        runBlocking {
            val races = raceRemoteDataSource.getRaces(2033)
            Truth.assertThat(races).isEmpty()
        }
    }

    @Test
    fun getRaces2022() {
        runBlocking {
            val races = raceRemoteDataSource.getRaces(2022)
            Truth.assertThat(races).isNotEmpty()
            races.any { it.sessions.qualifying != null }
        }
    }

    @Test
    fun getRaces2021() {
        runBlocking {
            val races = raceRemoteDataSource.getRaces(2021)
            Truth.assertThat(races).isNotEmpty()
        }
    }

    @Test
    fun getRaces1950() {
        runBlocking {
            val races = raceRemoteDataSource.getRaces(1950)
            Truth.assertThat(races).isNotEmpty()
        }
    }

    @Test
    fun getRaces1949() {
        runBlocking {
            val races = raceRemoteDataSource.getRaces(1949)
            Truth.assertThat(races).isEmpty()
        }
    }
}
