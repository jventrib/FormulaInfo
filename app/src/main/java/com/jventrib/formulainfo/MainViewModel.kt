package com.jventrib.formulainfo

import android.util.Log
import androidx.lifecycle.*
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalStoreApi
@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..2021).toList().reversed()

    val seasonPosition: MutableLiveData<Int> = MutableLiveData()

    val season: LiveData<Int> = seasonPosition.map {
        seasonList[it]
    }

    val races: LiveData<StoreResponse<List<RaceFull>>> =
        season.switchMap {
            repository.getAllRaces(it).asLiveData()
        }

    suspend fun refreshRaces() {
        repository.refresh()
        seasonPosition.value?.let { setSeasonPosition(it) }
    }

    private val raceFromList: MutableLiveData<RaceFull> = MutableLiveData()

    val race: LiveData<RaceFull> = raceFromList.switchMap { repository.getRace(it).asLiveData() }

    fun setRace(r: RaceFull) {
        raceFromList.value = r
    }

    fun setSeasonPosition(position: Int) {
        seasonPosition.value = position
    }

    val raceResults: LiveData<StoreResponse<List<RaceResultFull>>> =
        raceFromList.switchMap {
            repository.getRaceResults(it.race.season, it.race.round)
                .asLiveData()
        }
}