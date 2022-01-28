package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.remote.ResultRemote

object ResultConstructorMapper : Mapper<ResultRemote, Constructor> {
    override fun toEntity(remotes: List<ResultRemote>): List<Constructor> {
        return if (remotes.isEmpty()) {
            listOf(Constructor("nodata", "", "", "", null))
        } else {
            super.toEntity(remotes)
        }
    }

    override fun toEntity(remote: ResultRemote) =
        remote.constructor.let {
            Constructor(it.constructorId, it.url, it.name, it.nationality, null)
        }
}