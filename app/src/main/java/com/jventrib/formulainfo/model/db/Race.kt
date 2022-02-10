package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class Race(
    @Embedded
    val raceInfo: RaceInfo,
    @Relation(entityColumn = "id", parentColumn = "circuitId")
    val circuit: Circuit,
) : Serializable {
    @Transient
    var nextRace: Boolean = false
}
