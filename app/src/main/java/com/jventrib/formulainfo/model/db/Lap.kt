package com.jventrib.formulainfo.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Duration

@Entity(tableName = "lap_time")
data class Lap(
    @PrimaryKey
    val key: String,
    val season: Int,
    val round: Int,
    val driverId: String,
    val number: Int,
    val position: Int,
    val time: Duration,
) : Serializable
