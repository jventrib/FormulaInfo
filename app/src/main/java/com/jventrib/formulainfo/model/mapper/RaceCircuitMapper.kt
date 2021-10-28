package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Circuit
import com.jventrib.formulainfo.model.remote.RaceRemote

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
