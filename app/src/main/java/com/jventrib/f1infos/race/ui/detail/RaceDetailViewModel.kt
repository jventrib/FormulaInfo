package com.jventrib.f1infos.race.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jventrib.f1infos.race.model.Race

class RaceDetailViewModel : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()


    fun setRace(r: Race) {
        race.value = r
    }
}