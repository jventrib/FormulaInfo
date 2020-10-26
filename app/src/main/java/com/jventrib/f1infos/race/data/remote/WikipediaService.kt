package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.WikipediaAPIResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WikipediaService {
    @GET("w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&redirects=true")
    suspend fun getPageImage(
        @Query("titles") name: String,
        @Query("pithumbsize") size: Int = 100
    ): WikipediaAPIResponse
}
