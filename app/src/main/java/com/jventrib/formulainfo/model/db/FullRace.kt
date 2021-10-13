package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class FullRace(
    @Embedded
    val race: Race,
    @Relation(entityColumn = "id", parentColumn = "circuitId")
    val circuit: Circuit,
    ): Serializable