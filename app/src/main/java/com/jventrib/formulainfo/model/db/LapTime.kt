package com.jventrib.formulainfo.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "lap_time")
data class LapTime(
    @PrimaryKey
    val key: String,
    val season: Int,
    val round: Int,
    val driver: String,
    val number: Int,
    val position: Int,
    val time: java.time.Duration,
) : Serializable
