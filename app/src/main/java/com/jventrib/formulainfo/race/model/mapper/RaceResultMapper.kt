package com.jventrib.formulainfo.race.model.mapper

import com.jventrib.formulainfo.race.model.db.Constructor
import com.jventrib.formulainfo.race.model.db.Driver
import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote

object RaceResultMapper: Mapper<RaceResultRemote, RaceResult> {

    override fun toEntity(remote: RaceResultRemote) = RaceResult(
        "${remote.season}-${remote.round}-${remote.position}",
        remote.season,
        remote.round,
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
}
