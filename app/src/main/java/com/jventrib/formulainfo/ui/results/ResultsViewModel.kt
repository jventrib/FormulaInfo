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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import logcat.logcat
import java.time.Duration
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

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

    val resultsGraphFlow =
        round.distinctUntilChanged().asFlow().flatMapLatest {
            it?.let {
                repository.getResultGraph(season.value!!, it)
                    .onEach { logcat { "Map $it" } }
            } ?: emptyFlow()
        }

    val resultsGraph2 = round.distinctUntilChanged().asFlow().transform {

        val map = mutableMapOf(
            getResultSample().driver to listOf(
                Lap("k", 2021, 1, "one", 1, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 2, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 3, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
            )
        )
        emit(map.toMap())

        delay(2000)

        map.put(
            getResultSample().driver.copy(driverId = "two"), listOf(
                Lap("k", 2021, 1, "one", 1, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 2, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 3, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
                Lap("k", 2021, 1, "one", 4, 1, Duration.ofSeconds(10)),
            )
        )
        emit(map.toMap())


    }.asLiveData()

}
