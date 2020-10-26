package com.jventrib.f1infos.race.model.db

import androidx.room.Embedded
import androidx.room.Relation

data class RaceResultWithDriver(
    @Embedded
    val raceResult: RaceResult,
    @Relation(entityColumn = "driverId", parentColumn = "driverId")
    val driver: Driver

)