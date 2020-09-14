package com.jventrib.f1infos.race.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

@Entity(tableName = "race", primaryKeys = ["season", "round"])
class Race(
    val season: Int,
    val round: Int,
    val url: String,
    val raceName: String,
    val date: String,
    val time: String,
    @Expose(serialize = false, deserialize = false)
    var datetime: Instant,
    @SerializedName("Circuit")
    @Embedded
    val circuit: Circuit
) {
    data class Circuit(
        val circuitId: String,
        @SerializedName("url")
        val circuitUrl: String,
        val circuitName: String,
        @SerializedName("Location")
        @Embedded
        val location: Location
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
}