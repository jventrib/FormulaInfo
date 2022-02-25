package com.jventrib.formulainfo.model.db

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "lap_time", primaryKeys = ["season", "round", "driverId", "number"])
data class Lap(
    val season: Int,
    val round: Int,
    val driverId: String,
    val driverCode: String,
    val number: Int,
    val position: Int,
    val time: Long,
    val total: Long,
) : Serializable
