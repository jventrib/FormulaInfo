package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.model.remote.MRResponse
import com.jventrib.formulainfo.model.remote.RaceTable
import retrofit2.http.GET
import retrofit2.http.Path

interface MrdService {
    @GET("{season}.json")
    suspend fun getSchedule(@Path("season") season: Int): MRResponse<RaceTable>

    @GET("{season}/{round}/results.json")
    suspend fun getResults(
        @Path("season") season: Int,
        @Path("round") round: Int
    ): MRResponse<RaceTable>

    @GET("{season}/{round}/sprint.json")
    suspend fun getSprintResults(
        @Path("season") season: Int,
        @Path("round") round: Int
    ): MRResponse<RaceTable>

    @GET("{season}/{round}/qualifying.json")
    suspend fun getQualResults(
        @Path("season") season: Int,
        @Path("round") round: Int
    ): MRResponse<RaceTable>

    @GET("{season}/{round}/drivers/{driver}/laps.json?limit=100")
    suspend fun getLapTimes(
        @Path("season") season: Int,
        @Path("round") round: Int,
        @Path("driver") driver: String
    ): MRResponse<RaceTable>
}
