package com.jventrib.f1infos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.data.db.AppRoomDatabase
import com.jventrib.f1infos.race.data.remote.*
import com.jventrib.f1infos.race.ui.list.RaceListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.ZonedDateTime

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

    private val raceDao = AppRoomDatabase.getDatabase(context).raceDao()
    private val raceResultDao = AppRoomDatabase.getDatabase(context).raceResultDao()

    private val raceService: RaceService =
        buildRetrofit(context.getString(R.string.api_ergast))
    private val countryService: CountryService =
        buildRetrofit(context.getString(R.string.api_restcountries))
    private val wikipediaService: WikipediaService =
        buildRetrofit(context.getString(R.string.api_wikipedia))
    private val f1CalendarService: F1CalendarService =
        buildRetrofit(context.getString(R.string.api_github_raw))
    private val raceRemoteDataSource =
        RaceRemoteDataSource(raceService, countryService, wikipediaService, f1CalendarService)

    val raceRepository = RaceRepository(raceDao, raceResultDao, raceRemoteDataSource)

    private inline fun <reified T> buildRetrofit(url: String): T =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(T::class.java)

    fun getRaceListViewModelFactory(): (() -> ViewModelProvider.Factory)? = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                if (modelClass.isAssignableFrom(RaceListViewModel::class.java))
                    RaceListViewModel(raceRepository) as T
                else
                    throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}