package com.jventrib.formulainfo.utils

import androidx.room.TypeConverter
import java.time.Duration
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return if (value == null) null else Instant.ofEpochSecond(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.epochSecond
    }

    @TypeConverter
    fun toDuration(value: Long?): Duration? {
        return if (value == null) null else Duration.ofMillis(value)
    }

    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.toMillis()
    }
}