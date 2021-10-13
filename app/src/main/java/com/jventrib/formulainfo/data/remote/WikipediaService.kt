package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.model.remote.WikipediaAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaService {
    @GET("w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&redirects=true")
    suspend fun getPageImage(
        @Query("titles") name: String,
        @Query("pithumbsize") size: Int = 100,
        @Query("pilicense") license: String = Licence.ANY.param
    ): WikipediaAPIResponse

    enum class Licence(val param: String) {
        ANY("any"), FREE("free");
    }
}
