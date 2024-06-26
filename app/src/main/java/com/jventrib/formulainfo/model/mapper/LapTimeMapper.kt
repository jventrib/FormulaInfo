package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.remote.LapTimeRemote

object LapTimeMapper {

    fun toEntity(
        season: Int,
        round: Int,
        driverId: String,
        driverCode: String,
        remote: LapTimeRemote,
        total: Long
    ) = Lap(
        season,
        round,
        driverId,
        driverCode,
        remote.number,
        remote.timings[0].position,
        remote.time,
        total
    )

    fun toEntity(
        season: Int,
        round: Int,
        driverId: String,
        driverCode: String,
        remotes: List<LapTimeRemote>
    ): List<Lap> {
        var total = 0L
        return remotes.map {
            total += it.time
            toEntity(season, round, driverId, driverCode, it, total)
        }
    }
}
