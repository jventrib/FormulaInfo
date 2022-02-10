package com.jventrib.formulainfo.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.Duration

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

    val time get() = timings[0].time.toDuration()

    private fun String.toDuration(): Duration {
        val min = this.substringBefore(":").toLong()
        val sec = this.substringAfter(":").substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return Duration.ofMinutes(min).plus(Duration.ofSeconds(sec))
            .plus(Duration.ofMillis(millis))
    }
}
