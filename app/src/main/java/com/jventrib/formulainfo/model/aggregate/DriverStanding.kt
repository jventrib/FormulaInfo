package com.jventrib.formulainfo.model.aggregate

import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver

data class DriverStanding(
    val driver: Driver,
    val constructor: Constructor,
    val points: Float,
    val position: Int,
    val round: Int?
)