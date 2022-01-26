package com.jventrib.formulainfo.ui.schedule

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.aggregate.RaceWithResult
import com.jventrib.formulainfo.model.db.Race
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.Year
import java.time.temporal.TemporalField
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..Year.now().value).toList().reversed()

//    val season = MutableLiveData(2021)
    val season = MutableLiveData(Year.now().value)

    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val races: LiveData<StoreResponse<List<Race>>> =
        season.distinctUntilChanged().switchMap {
            repository.getRaces(it).asLiveData()
        }

    val racesWithResults: LiveData<StoreResponse<List<RaceWithResult>>> =
        season.distinctUntilChanged().switchMap {
            repository.getRacesWithResults(it).asLiveData()
        }

    suspend fun refresh() {
        repository.refresh()
    }
}
