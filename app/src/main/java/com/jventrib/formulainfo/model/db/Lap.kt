package com.jventrib.formulainfo.model.db

import androidx.room.Entity
import java.io.Serializable
import java.time.Duration

@Entity(tableName = "lap_time", primaryKeys = ["season", "round", "driverId", "number"])
data class Lap(
    val season: Int,
    val round: Int,
    val driverId: String,
    val number: Int,
    val position: Int,
    val time: Duration,
    val total: Duration,
) : Serializable
