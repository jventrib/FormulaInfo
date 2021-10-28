package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.remote.ResultRemote

object ResultConstructorMapper : Mapper<ResultRemote, Constructor> {

    override fun toEntity(remote: ResultRemote) =
        remote.constructor.let {
            Constructor(it.constructorId, it.url, it.name, it.nationality, null)
        }
}