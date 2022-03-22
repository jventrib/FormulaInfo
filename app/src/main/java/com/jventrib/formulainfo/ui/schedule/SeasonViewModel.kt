package com.jventrib.formulainfo.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.utils.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..currentYear()).toList().reversed()

    //    val season = MutableLiveData(2021)
    val season = MutableLiveData(currentYear())

    val round: MutableLiveData<Int?> = MutableLiveData(null)

//    val races: LiveData<List<Race>> =
//        season.distinctUntilChanged().switchMap {
//            repository.getRaces(it,true).asLiveData()
//        }

    val racesWithResults: LiveData<List<RaceWithResults>> =
        season.switchMap {
            repository.getRacesWithResults(it, false, true).asLiveData()
        }

    suspend fun refresh() {
        repository.refresh()
        season.postValue(season.value)
        round.postValue(null)
    }
}
