package com.jventrib.f1infos.common.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return if (value == null) null else Instant.ofEpochSecond(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.epochSecond
    }
}