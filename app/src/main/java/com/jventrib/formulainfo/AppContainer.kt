package com.jventrib.formulainfo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.data.db.AppRoomDatabase
import com.jventrib.formulainfo.race.data.remote.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@FlowPreview
@ExperimentalCoroutinesApi
class AppContainer(context: Context) {

    private val gsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder().registerTypeAdapter(
            Instant::class.java,
            JsonDeserializer { json, _, _ ->
                ZonedDateTime.parse(json.asJsonPrimitive.asString).toInstant()
            }).create()
    )

    private val mrdService: MrdService =
        buildRetrofit(context.getString(R.string.api_ergast))
    private val wikipediaService: WikipediaService =
        buildRetrofit(context.getString(R.string.api_wikipedia))
    private val f1CalendarService: F1CalendarService =
        buildRetrofit(context.getString(R.string.api_github_raw))
    private val raceRemoteDataSource =
        RaceRemoteDataSource(mrdService, wikipediaService, f1CalendarService)

    val raceRepository = RaceRepository(AppRoomDatabase.getDatabase(context), raceRemoteDataSource)

    private inline fun <reified T> buildRetrofit(url: String): T {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(gsonConverterFactory)
            .client(httpClient.build())
            .build()
            .create(T::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun getViewModelFactory(vm: (RaceRepository) -> ViewModel): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return vm(raceRepository) as T
            }
        }
}