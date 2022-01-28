package com.jventrib.formulainfo.ui.results

import androidx.lifecycle.*
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import logcat.logcat
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(Year.now().value)
    val round: MutableLiveData<Int?> = MutableLiveData(null)

    val seasonAndRound = MediatorLiveData<SeasonRound>().apply {
        addSource(season) { value = SeasonRound(season.value!!, round.value) }
        addSource(round) { value = SeasonRound(season.value!!, round.value) }
    }

    data class SeasonRound(val season: Int, val round: Int?)


    val map = MutableLiveData<Map<Driver, List<Lap>>>()

    val race: LiveData<Race?> =
        seasonAndRound.distinctUntilChanged().switchMap { sr ->
            sr.round?.let {
                repository.getRace(sr.season, it)
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val results: LiveData<StoreResponse<List<Result>>> =
        race.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResults(season.value!!, it.raceInfo.round).asLiveData()
            } ?: MutableLiveData(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }

    val resultsWithLaps =
        round.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResultsWithLaps(season.value!!, it)
//                    .map { it.toMap() }
                    .onEach { logcat { "Map $it" } }
                    .asLiveData()
            } ?: MutableLiveData(null)
        }


    val standings =
        seasonAndRound.distinctUntilChanged().switchMap {
            repository.getStandings(it.season, it.round).asLiveData()
        }

    val seasonStandings =
        season.distinctUntilChanged().switchMap {
            repository.getSeasonStandings(it).asLiveData()
        }

}
