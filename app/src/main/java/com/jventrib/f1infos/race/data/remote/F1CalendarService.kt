package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.race.model.remote.f1calendar.F1CResult
import retrofit2.http.GET
import retrofit2.http.Path

interface F1CalendarService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : F1CResult

}
