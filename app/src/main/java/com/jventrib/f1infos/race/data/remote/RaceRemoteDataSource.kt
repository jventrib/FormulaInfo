package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.data.remote.BaseDataSource

class RaceRemoteDataSource(
    private val raceService: RaceService,
    private val countryService: CountryService
) : BaseDataSource() {
    suspend fun getRaces() = getResult { raceService.getRaces() }

    suspend fun getCountryFlag(country: String): String {
        return getResult { countryService.getCountry(country) }.data!!.last().flag
    }
}
