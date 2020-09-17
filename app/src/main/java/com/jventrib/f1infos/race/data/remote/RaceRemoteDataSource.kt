package com.jventrib.f1infos.race.data.remote

class RaceRemoteDataSource(
    private val raceService: RaceService,
    private val countryService: CountryService
) {
    suspend fun getRaces(season: Int) = raceService.getRaces(season).mrData.table.races

    suspend fun getCountryFlag(country: String) = countryService.getCountry(country).last().flag
}
