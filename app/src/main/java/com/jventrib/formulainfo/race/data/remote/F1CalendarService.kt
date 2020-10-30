package com.jventrib.formulainfo.race.data.remote

import com.jventrib.formulainfo.race.model.remote.f1calendar.F1CResult
import retrofit2.http.GET
import retrofit2.http.Path

interface F1CalendarService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : F1CResult

}
