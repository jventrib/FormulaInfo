package com.jventrib.formulainfo

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.Race
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel(private val repository: RaceRepository) : ViewModel() {

    val season: MutableLiveData<Int> = MutableLiveData()

    fun setSeason(s: Int) {
        season.value = s
    }

    val seasonRaces: LiveData<StoreResponse<List<Race>>> =
        season.switchMap { repository.getAllRaces(it).asLiveData() }

    val race: MutableLiveData<Race> = MutableLiveData()

    fun setRace(r: Race) {
        race.value = r
    }

    val raceResults: LiveData<StoreResponse<List<RaceResultFull>>> =
        race.switchMap { repository.getRaceResults(it.season, it.round).asLiveData() }
}