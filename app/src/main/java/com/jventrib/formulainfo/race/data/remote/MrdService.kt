package com.jventrib.formulainfo.race.data.remote

import com.jventrib.formulainfo.common.model.MRResponse
import com.jventrib.formulainfo.race.model.RaceTable
import retrofit2.http.GET
import retrofit2.http.Path

interface MrdService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : MRResponse<RaceTable>

    @GET("{season}/{round}/results.json")
    suspend fun getRaceResults(@Path("season") season: Int, @Path("round") round: Int) : MRResponse<RaceTable>
}