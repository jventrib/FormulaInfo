package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Relation

data class FullRaceResult(
    @Embedded
    val raceResult: RaceResult,
    @Relation(entityColumn = "driverId", parentColumn = "driverId")
    val driver: Driver,
    @Relation(entityColumn = "id", parentColumn = "constructorId")
    val constructor: Constructor

)