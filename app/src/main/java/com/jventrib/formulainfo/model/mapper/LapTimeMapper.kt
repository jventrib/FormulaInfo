package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.remote.LapTimeRemote
import java.time.Duration

object LapTimeMapper {

    fun toEntity(
        season: Int,
        round: Int,
        driverId: String,
        remote: LapTimeRemote,
        total: Duration
    ) = Lap(
        season,
        round,
        driverId,
        remote.number,
        remote.timings[0].position,
        remote.time,
        total
    )

    fun toEntity(
        season: Int,
        round: Int,
        driverId: String,
        remotes: List<LapTimeRemote>
    ): List<Lap> {
        var total = Duration.ZERO
        return remotes.map {
            total += it.time
            toEntity(season, round, driverId, it, total)
        }
    }
}

