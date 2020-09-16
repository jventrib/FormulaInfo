package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.race.model.RaceTable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RaceService {
    @GET("current.json")
    suspend fun getRaces() : MRResponse<RaceTable>
}