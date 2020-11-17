package com.jventrib.formulainfo.race.model.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "race_result")
//@Entity(tableName = "race_result", primaryKeys = ["season", "round", "number"])
data class RaceResult(
    @PrimaryKey
    val key: String,
    val season: Int,
    val round: Int,
    val number: Int,
    val position: Int,
    val positionText: String,
    val points: Float,
    val driverId: String,
    val constructorId: String,
    val grid: Int,
    val laps: Int,
    val status: String,
    @Embedded
    val time: Time?,
    @Embedded
    val fastestLap: FastestLap?
) : Serializable {

    data class Time(
        val millis: Int,
        val time: String
    )

    data class FastestLap(
        val rank: Int,
        @ColumnInfo(name = "fastestLap")
        val lap: Int,
        @Embedded(prefix = "fastest_")
        val time: Time,
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
