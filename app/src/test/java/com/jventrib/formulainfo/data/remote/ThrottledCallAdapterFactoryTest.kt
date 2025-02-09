package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.di.RemoteModule.provideGsonConverterFactory
import com.jventrib.formulainfo.utils.now
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.create

class ThrottledCallAdapterFactoryTest {

    @Test
    @Ignore
    fun nonJolpicaApiShouldNotBeThrottled() {
        println("start --> ${now()}")
        runBlocking {
            repeat(15) {
                launch {
                    val fact = Retrofit.Builder()
                        .baseUrl("https://catfact.ninja")
                        .addConverterFactory(provideGsonConverterFactory())
                        .addCallAdapterFactory(ThrottledCallAdapterFactory(GlobalScope))
                        .build()
                        .create<CatFactService>()
                        .getFact()
                        .apply { delay((0L..1000L).random()) }
                    println("${now()} --> $fact")
                }
            }
        }
    }

    @Test
    @Ignore
    fun jolpicaApiShouldBeThrottled() {
        println("start --> ${now()}")
        val mrdService = Retrofit.Builder()
            .baseUrl("https://api.jolpi.ca/ergast/f1/")
            .addConverterFactory(provideGsonConverterFactory())
            .addCallAdapterFactory(ThrottledCallAdapterFactory(GlobalScope, 250.milliseconds))
            .build()
            .create<MrdService>()
        val season = 2022
        val round = 12
        runBlocking {
            val drivers =
                mrdService.getResults(
                    season,
                    round
                ).mrData.table.races.first().results!!.map { it.driver }
            println("${now()} --> $drivers")
            drivers.forEach { driver ->
                launch {
                    val tableMRResponse = mrdService
                        .getLapTimes(season, round, driver.driverId).mrData.table.races.firstOrNull()?.laps
                    println("${now()} --> $tableMRResponse")
                }
            }
        }
    }
}