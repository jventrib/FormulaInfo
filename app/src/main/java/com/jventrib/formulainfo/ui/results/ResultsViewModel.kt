package com.jventrib.formulainfo.ui.results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.switchMap
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val season = MutableLiveData(Year.now().value)
    val round: MutableLiveData<Int?> = MutableLiveData(null)

    private val seasonAndRound = MediatorLiveData<SeasonRound>().apply {
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

    val results: LiveData<List<Result>> =
        race.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResults(season.value!!, it.raceInfo.round, true).asLiveData()
            } ?: MutableLiveData(listOf())
        }

    val resultsWithLaps =
        race.distinctUntilChanged().switchMap {
            it?.let {
                repository.getResultsWithLaps(season.value!!, it.raceInfo.round)
//                    .map { it.toMap() }
                    .asLiveData()
            } ?: MutableLiveData(null)
        }

    val standings =
        seasonAndRound.distinctUntilChanged().switchMap {
            repository.getRoundStandings(it.season, it.round).asLiveData()
        }

    val seasonStandingsChart =
        season.distinctUntilChanged().switchMap {
            repository.getSeasonStandings(it, false).asLiveData()
        }
}
