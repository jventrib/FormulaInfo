package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Relation

data class Result(
    @Embedded
    val resultInfo: ResultInfo,
    @Relation(entityColumn = "driverId", parentColumn = "driverId")
    val driver: Driver,
    @Relation(entityColumn = "id", parentColumn = "constructorId")
    val constructor: Constructor

)