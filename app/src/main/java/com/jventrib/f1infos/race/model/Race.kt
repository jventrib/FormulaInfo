package com.jventrib.f1infos.race.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "race", primaryKeys = ["season", "round"])
data class Race(
    val season: String,
    val round: Int,
    val url: String,
    val raceName: String,
    val date: String
)