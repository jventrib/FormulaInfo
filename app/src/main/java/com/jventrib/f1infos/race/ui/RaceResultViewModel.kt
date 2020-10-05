package com.jventrib.f1infos.race.ui

import androidx.lifecycle.*
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.data.db.AppRoomDatabase
import com.jventrib.f1infos.race.data.remote.CountryService
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.data.remote.RaceService
import com.jventrib.f1infos.race.model.Race

class RaceResultViewModel : ViewModel() {
    val race: LiveData<Race>

    init {
        race = liveData { Race(2020, 2, "test", "race1", "20200606", "11:30:30", null,
            Race.Circuit("1", "curl", "c1",
                Race.Circuit.Location(242F, 2343F, "loc1", "France", "FR"))
        )}
    }

}