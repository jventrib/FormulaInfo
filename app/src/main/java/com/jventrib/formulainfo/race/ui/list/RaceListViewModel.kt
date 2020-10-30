package com.jventrib.formulainfo.race.ui.list

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.data.RaceRepository
import com.jventrib.formulainfo.race.model.Race
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class RaceListViewModel(repository: RaceRepository) : ViewModel() {
    val allRaces: LiveData<StoreResponse<List<Race>>> = repository.getAllRaces().asLiveData()
}
