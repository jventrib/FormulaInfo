package com.jventrib.formulainfo.data.remote

import com.google.common.truth.Truth.assertThat
import com.skydoves.disneycompose.di.RemoteModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.net.HttpURLConnection

class MrdServiceTest {
    private var mockWebServer = MockWebServer()
    private lateinit var mrdService: MrdService

    @Before
    fun setup() {
        mockWebServer.start()
        mrdService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(RemoteModule.provideGsonConverterFactory())
            .build()
            .create(MrdService::class.java)

    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testRaceResults() {
        // Assign
        val file = "results.json"
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readFileContent(file)!!)
        mockWebServer.enqueue(response)
// Act
        runBlocking {
            val mrResponse = mrdService.getRaceResults(2020, 5)
            assertThat(mrResponse.mrData.table.races).hasSize(1)
            assertThat(mrResponse.mrData.table.races.first().results!!).hasSize(20)
            val result = mrResponse.mrData.table.races.first().results!!.first()
            assertThat(mrResponse.mrData.table.races.first().results!!).hasSize(20)
            assertThat(result.number).isEqualTo(33)
            assertThat(result.position).isEqualTo(1)
            assertThat(result.positionText).isEqualTo("1")
            assertThat(result.points).isEqualTo(25)
            assertThat(result.driver.driverId).isEqualTo("max_verstappen")
            assertThat(result.driver.permanentNumber).isEqualTo(33)
            assertThat(result.driver.code).isEqualTo("VER")
            assertThat(result.driver.url).isEqualTo("http://en.wikipedia.org/wiki/Max_Verstappen")
            assertThat(result.driver.givenName).isEqualTo("Max")
            assertThat(result.driver.familyName).isEqualTo("Verstappen")

            assertThat(result.constructor.constructorId).isEqualTo("red_bull")
            assertThat(result.constructor.url).isEqualTo("http://en.wikipedia.org/wiki/Red_Bull_Racing")
            assertThat(result.constructor.name).isEqualTo("Red Bull")
            assertThat(result.constructor.nationality).isEqualTo("Austrian")

            assertThat(result.grid).isEqualTo(4)
            assertThat(result.laps).isEqualTo(52)
            assertThat(result.status).isEqualTo("Finished")

            assertThat(result.time!!.millis).isEqualTo(4781993)
            assertThat(result.time!!.time).isEqualTo("1:19:41.993")

            assertThat(result.fastestLap!!.rank).isEqualTo(2)
            assertThat(result.fastestLap!!.lap).isEqualTo(46)
            assertThat(result.fastestLap!!.time.time).isEqualTo("1:29.465")

            assertThat(result.fastestLap!!.averageSpeed.units).isEqualTo("kph")
            assertThat(result.fastestLap!!.averageSpeed.speed).isEqualTo(237.049f)
        }
// Assert

    }


    @Test
    fun testLapTimes() {
        // Assign
        val file = "laps.json"
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readFileContent(file)!!)
        mockWebServer.enqueue(response)
// Act
        runBlocking {
            val mrResponse = mrdService.getLapTimes(2020, 5, "max_verstappen")
            assertThat(mrResponse.mrData.table.races).hasSize(1)
            assertThat(mrResponse.mrData.table.races.first().laps!!).hasSize(30)
            val lap = mrResponse.mrData.table.races.first().laps!!.first()
            assertThat(lap.number).isEqualTo(1)
            assertThat(lap.timings.first().position).isEqualTo(2)
            assertThat(lap.timings.first().time).isEqualTo("1:42.612")
        }
    }


    private fun readFileContent(file: String) =
        this::class.java.classLoader?.getResource(file)?.readText()
}