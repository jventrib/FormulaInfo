package com.jventrib.formulainfo.ui.common

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField

val customDateTimeFormatter: DateTimeFormatter?
    get() {
        // manually code maps to ensure correct data always used
        // (locale data can be changed by application code)
        val dow: MutableMap<Long, String> = HashMap()
        dow[1L] = "Mon"
        dow[2L] = "Tue"
        dow[3L] = "Wed"
        dow[4L] = "Thu"
        dow[5L] = "Fri"
        dow[6L] = "Sat"
        dow[7L] = "Sun"
        val moy: MutableMap<Long, String> = HashMap()
        moy[1L] = "Jan"
        moy[2L] = "Feb"
        moy[3L] = "Mar"
        moy[4L] = "Apr"
        moy[5L] = "May"
        moy[6L] = "Jun"
        moy[7L] = "Jul"
        moy[8L] = "Aug"
        moy[9L] = "Sep"
        moy[10L] = "Oct"
        moy[11L] = "Nov"
        moy[12L] = "Dec"

        return DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseLenient()
            .optionalStart()
            .appendText(ChronoField.DAY_OF_WEEK, dow)
            .appendLiteral(", ")
            .optionalEnd()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral(' ')
            .appendText(ChronoField.MONTH_OF_YEAR, moy)
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4) // 2 digit year not handled
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalEnd()
            .appendLiteral(' ')
            .toFormatter()
    }

fun Instant.formatDateTime(): String =
    ZonedDateTime.ofInstant(this, ZoneId.systemDefault()).format(customDateTimeFormatter)

val customTimeHourMinFormatter: DateTimeFormatter?
    get() {
        return DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseLenient()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter()
    }

fun Instant.formatTime(): String =
    ZonedDateTime.ofInstant(this, ZoneId.systemDefault()).format(customTimeHourMinFormatter)
