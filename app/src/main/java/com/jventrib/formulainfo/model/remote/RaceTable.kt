package com.jventrib.formulainfo.model.remote

import com.google.gson.annotations.SerializedName

data class RaceTable(
    val season: String,
    val round: Int,
    val raceName: String,
    @SerializedName("Races")
    val races: List<RaceRemote>
)
