package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.LapTime
import com.jventrib.formulainfo.model.remote.LapTimeRemote
import java.time.Duration

object LapTimeMapper {

    fun toEntity(season: Int, round: Int, driver: String, remote: LapTimeRemote) = LapTime(
        "${season}-${round}-${driver}-${remote.number}",
        season,
        round,
        driver,
        remote.number,
        remote.timings[0].position,
        remote.timings[0].time.toDuration()
    )

    fun toEntity(season: Int, round: Int, driver: String, remotes: List<LapTimeRemote>) =
        remotes.map { toEntity(season, round, driver, it) }

    private fun String.toDuration(): Duration {
        val min = this.substringBefore(":").toLong()
        val sec = this.substringAfter(":").substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return Duration.ofMinutes(min).plus(Duration.ofSeconds(sec))
            .plus(Duration.ofMillis(millis))
    }
}

