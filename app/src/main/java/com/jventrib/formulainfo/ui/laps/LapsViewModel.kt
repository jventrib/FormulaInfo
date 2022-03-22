package com.jventrib.formulainfo.ui.laps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.switchMap
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.utils.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LapsViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(currentYear())
    val round: MutableLiveData<Int?> = MutableLiveData(null)
    val driverId: MutableLiveData<String?> = MutableLiveData(null)

    val race: LiveData<Race?> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRace(season.value!!, it)
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val result =
        driverId.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResult(season.value!!, round.value!!, it).asLiveData()
            } ?: MutableLiveData(null)
        }

    val laps =
        result.distinctUntilChanged().switchMap {
            it?.let {
                repository.getLaps(season.value!!, round.value!!, it.driver).asLiveData()
            } ?: MutableLiveData(null)
        }
}
