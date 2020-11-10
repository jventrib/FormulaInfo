package com.jventrib.formulainfo.race.model.mapper

import com.jventrib.formulainfo.race.model.db.*
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote

object RaceCircuitMapper : Mapper<RaceRemote, Circuit> {

    override fun toEntity(remote: RaceRemote) = Circuit(
        remote.circuit.circuitId,
        remote.circuit.circuitUrl,
        remote.circuit.circuitName,
        remote.circuit.location.run {
            Circuit.Location(latitude, longitude, locality, country, flag)
        },
        remote.circuit.circuitImageUrl
    )
}
