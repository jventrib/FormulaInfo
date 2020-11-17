package com.jventrib.formulainfo.race.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RaceResultRemote(
    val season: Int,
    val round: Int,
    val number: Int,
    val position: Int,
    val positionText: String,
    val points: Float,
    @SerializedName("Driver")
    val driver: Driver,
    @SerializedName("Constructor")
    val constructor: Constructor,
    val grid: Int,
    val laps: Int,
    val status: String,
    @SerializedName("Time")
    val time: Time?,
    @SerializedName("FastestLap")
    val fastestLap: FastestLap?
) : Serializable {
    data class Driver(
        val driverId: String,
        val permanentNumber: Int,
        val code: String?,
        val url: String,
        val givenName: String,
        val familyName: String,
        val dateOfBirth: String,
        val nationality: String
    )

    data class Constructor(
        val constructorId: String,
        val url: String,
        val name: String,
        val nationality: String
    )

    data class Time(
        val millis: Int,
        val time: String
    )

    data class FastestLap(
        val rank: Int,
        val lap: Int,
        @SerializedName("Time")
        val time: Time,
        @SerializedName("AverageSpeed")
        val averageSpeed: AverageSpeed
    ) {
        data class AverageSpeed(
            val units: String,
            val speed: Float
        )
    }

    enum class RaceStatus {
        Finished, Retired
    }
}
