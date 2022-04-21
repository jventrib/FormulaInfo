package com.jventrib.formulainfo.model.remote

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.jventrib.formulainfo.data.remote.IntegerTypeAdapter
import java.io.Serializable

data class ResultRemote(
    @JsonAdapter(IntegerTypeAdapter::class)
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
        @JsonAdapter(IntegerTypeAdapter::class)
        val millis: Int,
        val time: String
    )

    data class FastestLap(
        val rank: Int,
        val lap: Int,
        @SerializedName("Time")
        val time: Time,
        @SerializedName("AverageSpeed")
        val averageSpeed: AverageSpeed?
    ) {
        data class AverageSpeed(
            val units: String,
            val speed: Float
        )
    }
}
