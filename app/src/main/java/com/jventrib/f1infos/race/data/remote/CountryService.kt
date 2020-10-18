package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.race.model.Country
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryService {
    @GET("{name}?fulltext=true&fields=alpha2Code")
    suspend fun getCountry(@Path("name") name: String) : List<Country>
}
