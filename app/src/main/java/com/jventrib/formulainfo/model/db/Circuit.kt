package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "circuit")
data class Circuit(
    @PrimaryKey
    val id: String,
    @SerializedName("url")
    val url: String,
    val name: String,
    @SerializedName("Location")
    @Embedded
    val location: Location,
    var imageUrl: String?
) : Serializable {
    data class Location(
        @SerializedName("lat")
        val latitude: Float,
        @SerializedName("long")
        val longitude: Float,
        val locality: String,
        val country: String,
        var flag: String?
    ) : Serializable
}
