package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.remote.ResultRemote

object ResultDriverMapper {

    fun toEntity(remote: ResultRemote, numberInTeam: Int) = remote.driver.let { driver ->
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
            null,
            numberInTeam
        )
    }

    fun toEntity(remotes: List<ResultRemote>): List<Driver> {
        return if (remotes.isEmpty()) {
            listOf(
                Driver(
                    "nodata",
                    -1,
                    "nodata",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "nodata",
                    null,
                    -1
                )
            )
        } else {
            remotes
                .groupBy { it.constructor.constructorId }
                .flatMap { it.value.withIndex() }
                .map { toEntity(it.value, it.index) }
        }
    }
}
