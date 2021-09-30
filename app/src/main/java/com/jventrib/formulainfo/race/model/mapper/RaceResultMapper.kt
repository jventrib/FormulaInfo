package com.jventrib.formulainfo.race.model.mapper

import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote

object RaceResultMapper {

    fun toEntity(season: Int, round: Int, remote: RaceResultRemote) = RaceResult(
        "${season}-${round}-${remote.position}",
        season,
        round,
        remote.number,
        remote.position,
        remote.positionText,
        remote.points,
        remote.driver.driverId,
        remote.constructor.constructorId,
        remote.grid,
        remote.laps,
        remote.status,
        remote.time?.let { RaceResult.Time(it.millis, it.time) },
        remote.fastestLap?.run {
            RaceResult.FastestLap(
                rank,
                lap,
                RaceResult.Time(time.millis, time.time),
                RaceResult.FastestLap.AverageSpeed(averageSpeed.units, averageSpeed.speed)
            )
        },
    )

    fun toEntity(season: Int, round: Int, remotes: List<RaceResultRemote>) =
        remotes.map { toEntity(season, round, it) }
}
