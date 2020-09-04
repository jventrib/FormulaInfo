package com.jventrib.f1infos.race

import androidx.lifecycle.LiveData
import com.jventrib.f1infos.race.db.RaceDao
import com.jventrib.f1infos.race.model.Race

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class RaceRepository(private val raceDao: RaceDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allRaces: LiveData<List<Race>> = raceDao.getAllRaces()
 
    suspend fun insert(race: Race) {
        raceDao.insert(race)
    }
}