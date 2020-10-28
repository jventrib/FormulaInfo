package com.jventrib.f1infos.race.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Constructor(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "constructorUrl")
    val url: String,
    val name: String,
    @ColumnInfo(name = "constructorNationality")
    val nationality: String,
    @ColumnInfo(name = "image")
    val image: String?

)