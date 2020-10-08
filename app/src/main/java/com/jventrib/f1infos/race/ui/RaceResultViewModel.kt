package com.jventrib.f1infos.race.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jventrib.f1infos.race.model.Race

class RaceResultViewModel : ViewModel() {
    val race: MutableLiveData<Race> = MutableLiveData()


    fun setRace(r: Race) {
        race.value = r
    }
}