package com.jventrib.f1infos.race.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.jventrib.f1infos.race.RaceRepository
import com.jventrib.f1infos.race.db.RaceRoomDatabase
import com.jventrib.f1infos.race.model.Race
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RaceRepository
    val allRaces: LiveData<List<Race>>

    init {
        val raceDao = RaceRoomDatabase.getDatabase(application, viewModelScope).raceDao()
        repository = RaceRepository(raceDao)
        allRaces = repository.allRaces
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(race: Race) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(race)
    }
}