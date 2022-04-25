package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.model.remote.ResultRemote

object ResultMapper {

    fun toEntity(season: Int, round: Int, session: Session, remote: ResultRemote) = ResultInfo(
        "$season-$round-$session-${remote.position}",
        season,
        round,
        session,
        remote.number,
        remote.position,
        remote.positionText ?: remote.position.toString(),
        remote.points,
        remote.driver.driverId,
        remote.constructor.constructorId,
        remote.grid,
        remote.laps,
        remote.status ?: "None",
        remote.getTime(),
        remote.fastestLap?.run {
            ResultInfo.FastestLap(
                rank,
                lap,
                ResultInfo.Time(time.millis, time.time),
                averageSpeed?.let { ResultInfo.FastestLap.AverageSpeed(it.units, it.speed) }
            )
        },
    )

    private fun ResultRemote.getTime(): ResultInfo.Time? {
        val sessionsTimes = listOf(
            ResultRemote::time,
            ResultRemote::q3,
            ResultRemote::q2,
            ResultRemote::q1
        )
        val time = sessionsTimes.mapNotNull { it(this) }
            .firstOrNull()
        return when {
            time is ResultRemote.Time -> ResultInfo.Time(time.millis, time.time)
            time != null -> ResultInfo.Time(0, time.toString())
            else -> null
        }
    }

    fun toEntity(
        season: Int,
        round: Int,
        session: Session,
        remotes: List<ResultRemote>
    ): List<ResultInfo> =
        if (remotes.isEmpty()) {
            // Insert dummy line to store the no data from remote info
            listOf(
                ResultInfo(
                    "$season-$round-$session-nodata",
                    season,
                    round,
                    session,
                    -1,
                    -1,
                    "nodata",
                    0f,
                    "nodata",
                    "nodata",
                    -1,
                    -1,
                    "",
                    null,
                    null
                )
            )
        } else {
            remotes.map { toEntity(season, round, session, it) }
        }
}
