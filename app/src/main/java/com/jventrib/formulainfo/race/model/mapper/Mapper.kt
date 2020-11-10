package com.jventrib.formulainfo.race.model.mapper

interface Mapper<R, E> {
    fun toEntity(remote: R): E

    fun toEntity(remotes: List<R>) =
        remotes.map { toEntity(it) }
}