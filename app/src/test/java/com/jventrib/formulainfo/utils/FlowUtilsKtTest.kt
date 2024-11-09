package com.jventrib.formulainfo.utils

import com.google.common.truth.Truth
import com.jventrib.formulainfo.data.remote.MrdService
import com.jventrib.formulainfo.di.RemoteModule.provideGsonConverterFactory
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import org.junit.Ignore
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.create

class FlowUtilsKtTest {

    @Test
    fun countDownFlow() {
        runBlocking {
            val futurDate = now().plusSeconds(66666)
            val countDownFlow = futurDate.countDownFlow(1.seconds)
            val toList = countDownFlow
                .take(5)
                .onEach {
                    it.toComponents { days, hours, minutes, seconds, _ ->
                        println("$days days $hours:$minutes:$seconds")
                    }
                }
                .toList()
            toList[3].toComponents { days, hours, minutes, seconds, _ ->
                Truth.assertThat(days).isEqualTo(0)
                Truth.assertThat(hours).isEqualTo(18)
                Truth.assertThat(minutes).isEqualTo(31)
                Truth.assertThat(seconds).isEqualTo(2)
            }
            toList[4].toComponents { days, hours, minutes, seconds, _ ->
                Truth.assertThat(days).isEqualTo(0)
                Truth.assertThat(hours).isEqualTo(18)
                Truth.assertThat(minutes).isEqualTo(31)
                Truth.assertThat(seconds).isEqualTo(1)
            }
        }
    }

    private suspend fun fetchData(): String {
        delay((0..700).random().milliseconds)
        return "${now()} --> data"
    }

    @Test
    fun testThrottle() = runBlocking {
        val semaphore = Semaphore(4)
        repeat(15) {
            launch {
                throttled(semaphore) {
                    println(fetchData())
                }
            }
        }
    }

    @Test
    @Ignore
    fun jolpicaApiShouldBeThrottled() {
        val semaphore = Semaphore(1)
        println("start --> ${now()}")
        val mrdService = Retrofit.Builder()
            .baseUrl("https://api.jolpi.ca/ergast/f1/")
            .addConverterFactory(provideGsonConverterFactory())
            .build()
            .create<MrdService>()
        val season = 2020
        val round = 1
        runBlocking {
            val drivers =
                throttled(semaphore) { mrdService.getResults(season, round) }
                    .mrData.table.races.first().results!!.map { it.driver }
            println("${now()} --> $drivers")
            drivers.forEach { driver ->
                launch {
                    val tableMRResponse =
                        throttled(semaphore) {
                            mrdService.getLapTimes(
                                season,
                                round,
                                driver.driverId
                            )
                        }
                            .mrData.table.races.firstOrNull()?.laps
                    println("${now()} --> $tableMRResponse")
                }
            }
        }
    }
}
