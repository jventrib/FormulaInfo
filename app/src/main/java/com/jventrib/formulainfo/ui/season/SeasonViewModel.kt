package com.jventrib.formulainfo.ui.season

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
class SeasonViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..2021).toList().reversed()

    val season = MutableLiveData(2021)

    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val races: LiveData<StoreResponse<List<FullRace>>> =
        season.distinctUntilChanged().switchMap {
            repository.getRaces(it).asLiveData()
        }

    suspend fun refresh() {
        repository.refresh()
    }
}
