package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.remote.RaceRemote

object RaceMapper : Mapper<RaceRemote, Race> {

    override fun toEntity(remote: RaceRemote) = Race(
        remote.season,
        remote.round,
        remote.url,
        remote.raceName,
        remote.circuit.circuitId,
        Race.Sessions(
            remote.sessions.fp1,
            remote.sessions.fp2,
            remote.sessions.fp3,
            remote.sessions.qualifying,
            remote.sessions.gp
        )
    )
}
