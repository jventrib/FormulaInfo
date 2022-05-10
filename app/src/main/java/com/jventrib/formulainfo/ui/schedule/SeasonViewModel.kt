package com.jventrib.formulainfo.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.ui.common.composable.toSharedFlow
import com.jventrib.formulainfo.utils.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(private val repository: RaceRepository) : ViewModel() {

    val seasonList = (1950..currentYear()).toList().reversed()

    val _season = MutableSharedFlow<Int>(1).apply { tryEmit(currentYear()) }
    val season = _season.asSharedFlow()

    fun setSeason(season: Int) {
        _season.tryEmit(season)
    }

    val racesWithResults =
        season
            .flatMapLatest { repository.getRacesWithResults(it, false, true) }
            .toSharedFlow(viewModelScope)

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
            _season.emit(season.first())
        }
    }
}
