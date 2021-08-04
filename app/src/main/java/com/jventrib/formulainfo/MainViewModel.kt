package com.jventrib.formulainfo

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.RaceResultFull

class MainViewModel(private val repository: RaceRepository) : ViewModel(), IMainViewModel {

    override val seasonList = (1950..2021).toList().reversed()

    val seasonPosition: MutableLiveData<Int> = MutableLiveData()

    override val season: LiveData<Int> = seasonPosition.map {
        seasonList[it]
    }

    override val races: LiveData<StoreResponse<List<RaceFull>>> =
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

    override fun setSeasonPosition(position: Int) {
        seasonPosition.value = position
    }

    val raceResults: LiveData<StoreResponse<List<RaceResultFull>>> =
        raceFromList.switchMap {
            repository.getRaceResults(it.race.season, it.race.round)
                .asLiveData()
        }
}