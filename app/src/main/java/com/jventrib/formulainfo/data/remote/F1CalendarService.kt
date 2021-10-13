package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.model.remote.F1CResult
import retrofit2.http.GET
import retrofit2.http.Path

interface F1CalendarService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : F1CResult

}
