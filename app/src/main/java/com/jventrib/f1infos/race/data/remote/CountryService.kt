package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.race.model.Country
import com.jventrib.f1infos.race.model.RaceTable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryService {
    @GET("{name}?fulltext=true&fields=flag")
    suspend fun getCountry(@Path("name") name: String) : List<Country>
}
