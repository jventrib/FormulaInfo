package com.jventrib.formulainfo.race.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.Instant

class RaceRemote(
    val season: Int,
    val round: Int,
    val url: String,
    val raceName: String,
    @SerializedName("Circuit")
    val circuit: Circuit,
    var sessions: Sessions
) : Serializable {
    lateinit var time: String

    lateinit var date: String

    @SerializedName("Results")
    var resultRemotes: List<RaceResultRemote>? = null

    val timeInitialized get() = ::time.isInitialized

    data class Circuit(
        val circuitId: String,
        @SerializedName("url")
        val circuitUrl: String,
        val circuitName: String,
        @SerializedName("Location")
        val location: Location,
        var circuitImageUrl: String?
    ): Serializable {
        data class Location(
            @SerializedName("lat")
            val latitude: Float,
            @SerializedName("long")
            val longitude: Float,
            val locality: String,
            val country: String,
            var flag: String?
        ): Serializable
    }

    data class Sessions(
        val fp1: Instant? = null,
        val fp2: Instant? = null,
        val fp3: Instant? = null,
        val qualifying: Instant? = null,
        val gp: Instant
    ): Serializable

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RaceRemote

        if (season != other.season) return false
        if (round != other.round) return false

        return true
    }

    override fun hashCode(): Int {
        var result = season
        result = 31 * result + round
        return result
    }

    override fun toString(): String {
        return "Race(season=$season, round=$round, url='$url', raceName='$raceName', circuit=$circuit, sessions=$sessions)"
    }

}