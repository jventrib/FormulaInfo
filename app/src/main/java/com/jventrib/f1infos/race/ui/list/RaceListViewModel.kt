package com.jventrib.f1infos.race.ui.list

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.model.Race
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class RaceListViewModel(repository: RaceRepository) : ViewModel() {
    val allRaces: LiveData<StoreResponse<List<Race>>> = repository.getAllRaces().asLiveData()
}
