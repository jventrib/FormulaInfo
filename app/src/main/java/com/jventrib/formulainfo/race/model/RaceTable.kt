package com.jventrib.formulainfo.race.model

import com.google.gson.annotations.SerializedName
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.remote.RaceRemote

data class RaceTable(
    val season: String,
    val round: Int,
    val raceName: String,
    @SerializedName("Races")
    val races: List<RaceRemote>
)