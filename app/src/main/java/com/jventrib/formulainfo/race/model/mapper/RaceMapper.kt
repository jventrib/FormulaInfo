package com.jventrib.formulainfo.race.model.mapper

import com.jventrib.formulainfo.race.model.db.Constructor
import com.jventrib.formulainfo.race.model.db.Driver
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.remote.RaceRemote
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote

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
            remote.sessions.race
        )
    )
}
