package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.data.remote.BaseDataSource
import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.race.model.RaceTable
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RaceRemoteDataSource(
    private val raceService: RaceService,
    private val countryService: CountryService
) : BaseDataSource() {
    suspend fun getRaces() = getResult { raceService.getRaces() }

    suspend fun getCountryFlag(country: String): String {
        return getResult { countryService.getCountry(country) }.data!!.last().flag
    }
}
