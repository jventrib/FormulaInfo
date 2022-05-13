package com.jventrib.formulainfo.ui.laps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.ui.common.composable.toSharedFlow
import com.jventrib.formulainfo.ui.race.RaceViewModel
import com.jventrib.formulainfo.utils.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class LapsViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    private val _season = MutableStateFlow(currentYear())
    val season = _season.asStateFlow()

    private val _round = MutableStateFlow<Int?>(null)
    val round = _round.asStateFlow()

    private val seasonAndRound = season.combine(round) { s, r -> RaceViewModel.SeasonRound(s, r) }

    val driverId = MutableStateFlow<String?>(null)

    fun setSeason(season: Int) {
        this._season.value = season
    }

    fun setRound(round: Int?) {
        this._round.value = round
    }

    fun setDriverId(driverId: String) {
        this.driverId.value = driverId
    }

    val race = seasonAndRound
        .distinctUntilChanged()
        .filter { it.round != null }
        .flatMapLatest { repository.getRace(it.season, it.round!!) }
        .toSharedFlow(viewModelScope)

    val result =
        driverId
            .filterNotNull()
            .flatMapLatest { repository.getResult(season.value, round.value!!, it) }
            .toSharedFlow(viewModelScope)

    val laps =
        result
            .filterNotNull()
            .flatMapLatest {
                repository.getLaps(it.resultInfo.season, it.resultInfo.round, it.driver)
            }.toSharedFlow(viewModelScope)
}
