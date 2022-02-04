package com.jventrib.formulainfo.ui.schedule

import androidx.lifecycle.*
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..Year.now().value).toList().reversed()

    //    val season = MutableLiveData(2021)
    val season = MutableLiveData(Year.now().value)

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
        season.value = season.value
        round.value = null
    }
}
