package com.jventrib.formulainfo.ui.race

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.ui.common.composable.toSharedFlow
import com.jventrib.formulainfo.utils.currentYear
import com.jventrib.formulainfo.utils.mutableSharedFlow
import com.jventrib.formulainfo.utils.now
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {
    private val _season = mutableSharedFlow(currentYear())
    val season = _season.asSharedFlow()

    private val _round = mutableSharedFlow<Int?>()
    val round = _round.asSharedFlow()

    private val _session = mutableSharedFlow(Session.RACE)
    val session = _session.asSharedFlow()

    fun setSeason(season: Int) {
        this._season.tryEmit(season)
    }

    fun setRound(round: Int?) {
        this._round.tryEmit(round)
    }

    fun setSession(session: Session) {
        this._session.tryEmit(session)
    }

    private val seasonAndRound = season.combine(round) { s, r -> SeasonRound(s, r) }.distinctUntilChanged()

    data class SeasonRound(val season: Int, val round: Int?)

    val race = seasonAndRound
        .filter { it.round != null }
        .flatMapLatest { sr -> repository.getRace(sr.season, sr.round!!) }
        .toSharedFlow(viewModelScope)

    val results = race
        .combine(session) { r, session ->
            if (now().isAfter(r.raceInfo.sessions.race)) {
                viewModelScope.launch { repository.refreshPreviousRaces(r.raceInfo.round) }
            }
            when (session) {
                Session.QUAL -> repository.getQualResults(r.raceInfo.season, r.raceInfo.round, true)
                Session.SPRINT -> repository.getSprintResults(
                    r.raceInfo.season,
                    r.raceInfo.round,
                    true
                )
                else -> repository.getRaceResults(r.raceInfo.season, r.raceInfo.round, true)
            }
        }.flattenConcat()
        .toSharedFlow(viewModelScope)

    val resultsWithLaps =
        race
            .filterNotNull()
            .flatMapLatest {
                repository.getResultsWithLaps(it.raceInfo.season, it.raceInfo.round)
            }
            .toSharedFlow(viewModelScope)

    val standings = seasonAndRound
        .flatMapLatest {
            repository.getRoundStandings(it.season, it.round)
        }

    val seasonStandingsChart =
        season
            .distinctUntilChanged()
            .onEach {
                logcat { "Season emit: $it" }
            }
            .flatMapLatest {
                repository.getSeasonStandings(it, false)
            }
            .toSharedFlow(viewModelScope)
}
