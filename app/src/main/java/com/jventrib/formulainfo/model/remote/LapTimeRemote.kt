package com.jventrib.formulainfo.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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

    private fun String.toDuration(): Long {
        val min = this.substringBefore(":").toLong()
        val sec = this.substringAfter(":").substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return min.minutes.inWholeMilliseconds.plus(sec.seconds.inWholeMilliseconds)
            .plus(millis.milliseconds.inWholeMilliseconds)
    }
}
