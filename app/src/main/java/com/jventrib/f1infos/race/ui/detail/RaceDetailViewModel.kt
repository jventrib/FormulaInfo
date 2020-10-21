package com.jventrib.f1infos.race.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.RaceResult

class RaceDetailViewModel(val repository: RaceRepository) : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()

    lateinit var raceResult: LiveData<StoreResponse<List<RaceResult>>>

    fun setRace(r: Race) {
        race.value = r
        raceResult = repository.getRaceResults(r.season, r.round).asLiveData()
    }
}