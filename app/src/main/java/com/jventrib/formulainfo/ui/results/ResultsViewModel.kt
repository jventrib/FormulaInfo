package com.jventrib.formulainfo.ui.results

import androidx.lifecycle.*
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.result.getResultSample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import logcat.logcat
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(2021)

    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val map = MutableLiveData<Map<Driver, List<Lap>>>()

    val race: LiveData<Race?> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getRace(season.value!!, it)
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val results: LiveData<StoreResponse<List<Result>>> =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResults(season.value!!, it).asLiveData()
            } ?: MutableLiveData(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }

    val resultsGraph =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResultGraph(season.value!!, it)
//                    .map { it.toMap() }
                    .onEach { logcat { "Map $it" } }
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

}
