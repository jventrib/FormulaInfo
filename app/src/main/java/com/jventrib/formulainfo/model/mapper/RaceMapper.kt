package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.RaceInfo
import com.jventrib.formulainfo.model.remote.RaceRemote

object RaceMapper : Mapper<RaceRemote, RaceInfo> {

    override fun toEntity(remote: RaceRemote) = RaceInfo(
        remote.season,
        remote.round,
        remote.url,
        remote.raceName,
        remote.circuit.circuitId,
        RaceInfo.Sessions(
            remote.sessions.fp1,
            remote.sessions.fp2,
            remote.sessions.fp3,
            remote.sessions.qualifying,
            remote.sessions.gp
        )
    )
}
