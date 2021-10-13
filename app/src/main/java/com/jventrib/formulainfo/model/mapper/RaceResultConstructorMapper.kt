package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.remote.RaceResultRemote

object RaceResultConstructorMapper : Mapper<RaceResultRemote, Constructor> {

    override fun toEntity(remote: RaceResultRemote) =
        remote.constructor.let {
            Constructor(it.constructorId, it.url, it.name, it.nationality, null)
        }
}