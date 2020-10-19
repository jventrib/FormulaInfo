package com.jventrib.f1infos.race.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.race.model.Race

class RaceDetailViewModel : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()

//    val raceResult: LiveData<StoreResponse<List<Race>>> = repository.getAllRaces().asLiveData()

    fun setRace(r: Race) {
        race.value = r
    }
}