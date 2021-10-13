package com.jventrib.formulainfo.ui.race

import androidx.lifecycle.*
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.model.db.FullRaceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(2021)

    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val fullRace: LiveData<FullRace?> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRace(season.value!!, it)
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val raceResultsRaceResult: LiveData<StoreResponse<List<FullRaceResult>>> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRaceResults(season.value!!, it).asLiveData()
            } ?: MutableLiveData(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }

}
