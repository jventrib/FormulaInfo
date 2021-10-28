package com.jventrib.formulainfo.ui.lap

import androidx.lifecycle.*
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.RaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LapViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(2021)
    val round: MutableLiveData<Int?> = MutableLiveData(null)
    val driverId: MutableLiveData<String?> = MutableLiveData(null)

    val result =
        driverId.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResult(season.value!!, round.value!!, it).asLiveData()
            } ?: MutableLiveData(null)
        }

    val laps =
        driverId.distinctUntilChanged().switchMap {
            it?.let {
                repository.getLapTimes(season.value!!, round.value!!, it).asLiveData()
            } ?: MutableLiveData(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }


}
