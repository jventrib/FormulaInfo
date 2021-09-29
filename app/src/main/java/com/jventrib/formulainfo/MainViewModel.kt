package com.jventrib.formulainfo

import androidx.lifecycle.*
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.FullRaceResult

class MainViewModel(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..2021).toList().reversed()

    val season = MutableLiveData(2021)

    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val races: LiveData<StoreResponse<List<RaceFull>>> =
        season.distinctUntilChanged().switchMap {
            repository.getAllRaces(it).asLiveData()
        }

    val raceFull: LiveData<RaceFull?> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRace(season.value!!, it)
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val raceResultsRaceResult: LiveData<StoreResponse<List<FullRaceResult>>> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRaceResults(season.value!!, it)
                    .asLiveData()
            } ?: MutableLiveData(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }

    suspend fun refreshRaces() {
        repository.refresh()
    }
}
