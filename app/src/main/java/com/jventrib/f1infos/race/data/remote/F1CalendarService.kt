package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.model.MRResponse
import com.jventrib.f1infos.race.model.RaceTable
import com.jventrib.f1infos.race.model.f1calendar.F1CRaces
import retrofit2.http.GET
import retrofit2.http.Path

interface F1CalendarService {
    @GET("{season}.json")
    suspend fun getRaces(@Path("season") season: Int) : F1CRaces

}
