package com.jventrib.f1infos.race.model

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.Instant
import java.time.ZonedDateTime

@Entity(tableName = "race", primaryKeys = ["season", "round"])
class Race(
    val season: Int,
    val round: Int,
    val url: String,
    val raceName: String,
    @SerializedName("Circuit")
    @Embedded
    val circuit: Circuit,
    @Embedded
    var sessions: Sessions
): Serializable {
    data class Circuit(
        val circuitId: String,
        @SerializedName("url")
        val circuitUrl: String,
        val circuitName: String,
        @SerializedName("Location")
        @Embedded
        val location: Location,
        var circuitImageUrl: String?
    ) {
        data class Location(
            @SerializedName("lat")
            val latitude: Float,
            @SerializedName("long")
            val longitude: Float,
            val locality: String,
            val country: String,
            var flag: String?
        )
    }
        data class Sessions(
            val fp1: Instant?,
            val fp2: Instant?,
            val fp3: Instant?,
            val qualifying: Instant,
            val race: Instant
        )

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

}