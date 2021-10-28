package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.remote.ResultRemote

object ResultMapper {

    fun toEntity(season: Int, round: Int, remote: ResultRemote) = Result(
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
        remote.time?.let { Result.Time(it.millis, it.time) },
        remote.fastestLap?.run {
            Result.FastestLap(
                rank,
                lap,
                Result.Time(time.millis, time.time),
                Result.FastestLap.AverageSpeed(averageSpeed.units, averageSpeed.speed)
            )
        },
    )

    fun toEntity(season: Int, round: Int, remotes: List<ResultRemote>) =
        remotes.map { toEntity(season, round, it) }
}
