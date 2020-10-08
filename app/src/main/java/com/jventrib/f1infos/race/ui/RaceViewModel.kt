package com.jventrib.f1infos.race.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import com.google.gson.GsonBuilder
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.data.db.AppRoomDatabase
import com.jventrib.f1infos.race.data.remote.CountryService
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.data.remote.RaceService
import com.jventrib.f1infos.race.data.remote.WikipediaService
import com.jventrib.f1infos.race.model.Race
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RaceRepository
    val allRaces: LiveData<StoreResponse<List<Race>>>

    init {
        val raceDao = AppRoomDatabase.getDatabase(application, viewModelScope).raceDao()
        val raceService: RaceService = buildRetrofit("https://ergast.com/api/f1/")
        val countryService: CountryService = buildRetrofit("https://restcountries.eu/rest/v2/name/")
        val wikipediaService: WikipediaService = buildRetrofit("https://en.wikipedia.org/")
        val raceRemoteDataSource = RaceRemoteDataSource(raceService, countryService, wikipediaService)

        repository = RaceRepository(raceDao, raceRemoteDataSource)
        allRaces = repository.getAllRaces(viewModelScope).asLiveData()
    }

    private inline fun <reified T> buildRetrofit(url: String): T =
        Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(T::class.java)
}