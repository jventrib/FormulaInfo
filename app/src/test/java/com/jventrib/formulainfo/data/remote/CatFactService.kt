package com.jventrib.formulainfo.data.remote

import retrofit2.http.GET

interface CatFactService {

    @GET("/fact")
    suspend fun getFact(): Fact;
}

data class Fact(val fact: String, val length: Int)
