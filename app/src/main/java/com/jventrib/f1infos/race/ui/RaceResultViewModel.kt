package com.jventrib.f1infos.race.ui

import androidx.lifecycle.*
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.data.db.AppRoomDatabase
import com.jventrib.f1infos.race.data.remote.CountryService
import com.jventrib.f1infos.race.data.remote.RaceRemoteDataSource
import com.jventrib.f1infos.race.data.remote.RaceService
import com.jventrib.f1infos.race.model.Race

class RaceResultViewModel : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()


    fun setRace(r: Race) {
        race.value = r
    }
}