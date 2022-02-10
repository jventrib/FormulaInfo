package com.jventrib.formulainfo.data.sample

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import com.jventrib.formulainfo.model.remote.MRResponse
import com.jventrib.formulainfo.model.remote.RaceTable
import java.time.Instant
import java.time.ZonedDateTime

fun getContent(fileContent: String): MRResponse<RaceTable> {
    val gson = GsonBuilder()
        .registerTypeAdapter(
            Instant::class.java,
            JsonDeserializer { json, _, _ ->
                ZonedDateTime.parse(json.asJsonPrimitive.asString).toInstant()
            }
        )
        .create()

    return gson.fromJson(fileContent, object : TypeToken<MRResponse<RaceTable>>() {}.type)
}
