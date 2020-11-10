package com.jventrib.formulainfo

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
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

    val seasonRaces: LiveData<StoreResponse<List<RaceFull>>> =
        season.switchMap { repository.getAllRaces(it).asLiveData() }

    val raceFromList: MutableLiveData<RaceFull> = MutableLiveData()
    val race: LiveData<RaceFull> = raceFromList.switchMap { repository.getRace(it).asLiveData() }

    fun setRace(r: RaceFull) {
        raceFromList.value = r
    }

    val raceResults: LiveData<StoreResponse<List<RaceResultFull>>> =
        race.switchMap { repository.getRaceResults(it.race.season, it.race.round).asLiveData() }
}