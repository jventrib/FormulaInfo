package com.jventrib.formulainfo.race.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote
import java.io.Serializable
import java.time.Instant
import java.time.ZonedDateTime

@Entity(tableName = "race", primaryKeys = ["season", "round"])
class Race(
    val season: Int,
    val round: Int,
    val url: String,
    val raceName: String,
    val date: String,
    val time: String,
    @Expose(serialize = false, deserialize = false)
    var datetime: Instant?,
    @SerializedName("Circuit")
    @Embedded
    val circuit: Circuit,
    @Embedded
    var sessions: Sessions
) : Serializable {
    @SerializedName("Results")
    @Ignore
    var resultRemotes: List<RaceResultRemote>? = null

    data class Circuit(
        val circuitId: String,
        @SerializedName("url")
        val circuitUrl: String,
        val circuitName: String,
        @SerializedName("Location")
        @Embedded
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
        val race: Instant
    ): Serializable

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Race

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

    fun buildDatetime() {
        datetime = ZonedDateTime.parse("${date}T${time}").toInstant()
    }

}