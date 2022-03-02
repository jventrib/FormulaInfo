package com.jventrib.formulainfo.model.aggregate

import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver

data class DriverAndConstructor(
    val driver: Driver,
    val constructor: Constructor
)
