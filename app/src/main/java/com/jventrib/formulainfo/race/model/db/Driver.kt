package com.jventrib.formulainfo.race.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver")
data class Driver(
    @PrimaryKey
    val driverId: String,
    val permanentNumber: Int,
    val code: String?,
    @ColumnInfo(name = "driverUrl")
    val url: String,
    val givenName: String,
    val familyName: String,
    val dateOfBirth: String,
    @ColumnInfo(name = "driverNationality")
    val nationality: String,
    @ColumnInfo(name = "driverImage")
    val image: String?
)