package com.jventrib.formulainfo.race.model.mapper

import com.jventrib.formulainfo.race.model.db.Constructor
import com.jventrib.formulainfo.race.model.db.Driver
import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote

object RaceResultMapper {
    fun toEntity(raceResultRemotes: List<RaceResultRemote>) =
        raceResultRemotes.map { toEntity(it) }


    private fun toEntity(raceResultRemote: RaceResultRemote) = RaceResult(
        "${raceResultRemote.season}-${raceResultRemote.round}-${raceResultRemote.position}",
        raceResultRemote.season,
        raceResultRemote.round,
        raceResultRemote.number,
        raceResultRemote.position,
        raceResultRemote.positionText,
        raceResultRemote.points,
        raceResultRemote.driver.driverId,
        raceResultRemote.constructor.constructorId,
        raceResultRemote.grid,
        raceResultRemote.laps,
        raceResultRemote.status,
        raceResultRemote.time?.let { RaceResult.Time(it.millis, it.time) },
        raceResultRemote.fastestLap?.let {
            RaceResult.FastestLap(
                it.rank,
                it.lap,
                RaceResult.Time(it.time.millis, it.time.time),
                RaceResult.FastestLap.AverageSpeed(it.averageSpeed.units, it.averageSpeed.speed)
            )
        },
    )


    fun toDriverEntity(raceResultRemotes: List<RaceResultRemote>) =
        raceResultRemotes.map { toDriverEntity(it) }

    fun toDriverEntity(raceResultRemote: RaceResultRemote) = raceResultRemote.driver.let { driver ->
        Driver(
            driver.driverId,
            driver.permanentNumber,
            driver.code,
            driver.url,
            driver.givenName,
            driver.familyName,
            driver.dateOfBirth,
            driver.nationality,
            null,
        )
    }

    fun toConstructorEntity(raceResultRemotes: List<RaceResultRemote>) =
        raceResultRemotes.map { toConstructorEntity(it) }

    fun toConstructorEntity(raceResultRemotes: RaceResultRemote) =
        raceResultRemotes.constructor.let {
            Constructor(it.constructorId, it.url, it.name, it.nationality, null)
        }


}