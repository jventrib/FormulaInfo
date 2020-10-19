package com.jventrib.f1infos.race.model

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "race_result", primaryKeys = ["season", "round", "number"])
data class RaceResult(
    val season: Int,
    val round: Int,
    val number: Int,
    val position: Int,
    val positionText: String,
    val points: Int,
    @Embedded
    val driver: Driver,
    @Embedded
    val constructor: Constructor,
    val grid: Int,
    val laps: Int,
    val status: String,
    @Embedded
    val time: Time,
    @Embedded
    val fastestLap: FastestLap
): Serializable {
    data class Driver(
        val driverId: String,
        val permanentNumber: String,
        val code: String,
        @SerializedName("url")
        val driverUrl: String,
        val givenName: String,
        val familyName: String,
        val dateOfBirth: String,
        @SerializedName("nationality")
        val driverNationality: String
    )

    data class Constructor(
        val constructorId: String,
        @SerializedName("url")
        val constructorUrl: String,
        val name: String,
        @SerializedName("nationality")
        val constructorNationality: String
    )

    data class Time(
        val millis: Int,
        val time: String
    )

    data class FastestLap(
        val fastestLapRank: Int,
        val fastestLap: Int,
        @Embedded(prefix = "fastest_")
        @SerializedName("time")
        val fastestLapTime: Time,
        @Embedded
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
