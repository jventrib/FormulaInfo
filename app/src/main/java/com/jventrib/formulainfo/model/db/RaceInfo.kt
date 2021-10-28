package com.jventrib.formulainfo.model.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import com.jventrib.formulainfo.model.remote.ResultRemote
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "race", primaryKeys = ["season", "round"])
data class RaceInfo(
    val season: Int,
    val round: Int,
    val url: String,
    val raceName: String,
    val circuitId: String,
    @Embedded
    var sessions: Sessions
) : Serializable {
    @Ignore
    lateinit var time: String

    @Ignore
    lateinit var date: String

    @Ignore
    var resultRemotes: List<ResultRemote>? = null

    val timeInitialized get() = ::time.isInitialized

    data class Sessions(
        val fp1: Instant? = null,
        val fp2: Instant? = null,
        val fp3: Instant? = null,
        val qualifying: Instant? = null,
        val race: Instant
    ): Serializable
}