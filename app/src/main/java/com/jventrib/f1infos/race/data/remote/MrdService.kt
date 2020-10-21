package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.race.model.RaceTable
import retrofit2.http.GET
import retrofit2.http.Path

interface MrdService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : MRResponse<RaceTable>

    @GET("{season}/{round}/result.json")
    suspend fun getRaceResults(@Path("season") season: Int, @Path("round") round: Int) : MRResponse<RaceTable>
}