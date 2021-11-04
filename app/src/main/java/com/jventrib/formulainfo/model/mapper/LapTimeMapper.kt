package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.remote.LapTimeRemote
import java.time.Duration

object LapTimeMapper {

    fun toEntity(season: Int, round: Int, remote: LapTimeRemote) = Lap(
        "${season}-${round}-${remote.timings.first().driverId}-${remote.number}",
        season,
        round,
        remote.timings[0].driverId,
        remote.number,
        remote.timings[0].position,
        remote.timings[0].time.toDuration()
    )

    fun toEntity(season: Int, round: Int, remotes: List<LapTimeRemote>) =
        remotes.map { toEntity(season, round, it) }

    private fun String.toDuration(): Duration {
        val min = this.substringBefore(":").toLong()
        val sec = this.substringAfter(":").substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return Duration.ofMinutes(min).plus(Duration.ofSeconds(sec))
            .plus(Duration.ofMillis(millis))
    }
}

