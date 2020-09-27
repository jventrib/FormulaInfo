package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.race.model.Race

class MockRaceRemoteDataSource(raceService: RaceService,
                               countryService: CountryService
) : RaceRemoteDataSource(raceService, countryService){

    override suspend fun getRaces(season: Int) = listOf<Race>()

}