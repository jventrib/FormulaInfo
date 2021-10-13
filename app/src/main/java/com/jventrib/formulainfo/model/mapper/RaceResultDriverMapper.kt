package com.jventrib.formulainfo.model.mapper

import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.remote.RaceResultRemote

object RaceResultDriverMapper: Mapper<RaceResultRemote, Driver> {

    override fun toEntity(remote: RaceResultRemote) = remote.driver.let { driver ->
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
}
