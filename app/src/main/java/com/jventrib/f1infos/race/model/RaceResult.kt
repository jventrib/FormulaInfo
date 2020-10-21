package com.jventrib.f1infos.race.model

import androidx.room.ColumnInfo
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
    @SerializedName("Driver")
    val driver: Driver,
    @Embedded
    @SerializedName("Constructor")
    val constructor: Constructor,
    val grid: Int,
    val laps: Int,
    val status: String,
    @Embedded
    @SerializedName("Time")
    val time: Time?,
    @Embedded
    @SerializedName("FastestLap")
    val fastestLap: FastestLap?
) : Serializable {
    data class Driver(
        val driverId: String,
        val permanentNumber: Int,
        val code: String,
        @ColumnInfo(name = "driverUrl")
        val url: String,
        val givenName: String,
        val familyName: String,
        val dateOfBirth: String,
        @ColumnInfo(name = "driverNationality")
        val nationality: String
    )

    data class Constructor(
        val constructorId: String,
        @ColumnInfo(name = "constructorUrl")
        val url: String,
        val name: String,
        @ColumnInfo(name = "constructorNationality")
        val nationality: String
    )

    data class Time(
        val millis: Int,
        val time: String
    )

    data class FastestLap(
        val rank: Int,
        @ColumnInfo(name = "fastestLap")
        val lap: Int,
        @Embedded(prefix = "fastest_")
        @SerializedName("Time")
        val time: Time,
        @Embedded
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
