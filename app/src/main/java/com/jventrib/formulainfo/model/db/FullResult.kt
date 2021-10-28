package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Relation

data class FullResult(
    @Embedded
    val result: Result,
    @Relation(entityColumn = "driverId", parentColumn = "driverId")
    val driver: Driver,
    @Relation(entityColumn = "id", parentColumn = "constructorId")
    val constructor: Constructor

)