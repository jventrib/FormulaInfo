package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.common.model.WikipediaAPIResponse
import com.jventrib.f1infos.race.model.Country
import com.jventrib.f1infos.race.model.RaceTable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WikipediaService {
    @GET("w/api.php?action=query&prop=pageimages&format=json&piprop=original&redirects=true")
    suspend fun getPageImage(@Query("titles") name: String) : WikipediaAPIResponse
}
