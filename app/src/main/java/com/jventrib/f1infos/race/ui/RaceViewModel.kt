package com.jventrib.f1infos.race.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.jventrib.f1infos.common.data.Resource
import com.jventrib.f1infos.common.data.remote.create
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.data.db.AppRoomDatabase
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.data.remote.RaceService
import com.jventrib.f1infos.race.model.Race
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RaceRepository
    val allRaces: LiveData<Resource<List<Race>>>

    init {
        val raceDao = AppRoomDatabase.getDatabase(application).raceDao()
        val raceService: RaceService = buildRetrofit().create()
        val raceRemoteDataSource = RaceRemoteDataSource(raceService)
        repository = RaceRepository(raceDao, raceRemoteDataSource)
        allRaces = repository.getAllRaces()
    }

    private fun buildRetrofit() =
        Retrofit.Builder().baseUrl("https://ergast.com/api/f1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(race: Race) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(race)
    }
}