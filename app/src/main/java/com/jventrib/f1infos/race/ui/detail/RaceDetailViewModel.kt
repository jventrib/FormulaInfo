package com.jventrib.f1infos.race.ui.detail

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.db.RaceResultFull

class RaceDetailViewModel(private val repository: RaceRepository) : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()

    val raceResultRemoteAndConstructor: LiveData<StoreResponse<List<RaceResultFull>>> =
        race.switchMap { repository.getRaceResults(it.season, it.round).asLiveData() }

    fun setRace(r: Race) {
        race.value = r
    }
}