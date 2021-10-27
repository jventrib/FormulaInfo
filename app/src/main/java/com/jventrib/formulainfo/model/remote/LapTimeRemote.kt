package com.jventrib.formulainfo.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LapTimeRemote(
    val number: Int,
    @SerializedName("Timings")
    val timings: List<Timing>,
) : Serializable {
    data class Timing(
        val driverId: String,
        val position: Int,
        val time: String
    )
}
