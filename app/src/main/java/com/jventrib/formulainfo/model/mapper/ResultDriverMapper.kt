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
        val list = remotes.groupBy { it.constructor }.flatMap {
            it.value.mapIndexed { index, result -> Pair(result, index) }
        }
        return list.map { toEntity(it.first, it.second) }
    }

}
