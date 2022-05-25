package com.jventrib.formulainfo.ui.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val formatSymbols = DecimalFormatSymbols.getInstance().apply {
    decimalSeparator = ','
}

val twoDecimalDigitsFormat = DecimalFormat("#.##").apply {
    decimalFormatSymbols = formatSymbols
}

val twoTrailingZerosFormat = DecimalFormat("#.0").apply {
    decimalFormatSymbols = formatSymbols
}

fun Float.formatDecimal(withDecimalZeros: Boolean = false) = if (withDecimalZeros) {
    twoTrailingZerosFormat
} else {
    // Is number still the same after discarding places?
    if (toInt().toFloat() == this) {
        twoDecimalDigitsFormat
    } else {
        twoTrailingZerosFormat
    }
}.format(this)!!

fun Long.toLapTimeString(pattern: String = "mm:ss.SSS"): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    return LocalTime.ofNanoOfDay(this.milliseconds.inWholeNanoseconds).format(formatter)
}

fun Long.toDurationString(): String {
    return this.minutes.toComponents { hours, minutes, _, _ ->
        (if (hours > 0) "${hours}h" else "") + "${minutes.toString().padStart(2, '0')}m"
    }
}

fun String.toDuration(): Long {
    val split = this.split(':')
    if (split.size == 3) {
        // has hour
        val hour = split[0].toLong()
        val min = split[1].toLong()
        val sec = split[2].substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return hour.hours.inWholeMilliseconds
            .plus(min.minutes.inWholeMilliseconds)
            .plus(sec.seconds.inWholeMilliseconds)
            .plus(millis.milliseconds.inWholeMilliseconds)
    } else {
        val min = this.substringBefore(":").toLong()
        val sec = this.substringAfter(":").substringBefore(".").toLong()
        val millis = this.substringAfter(".").toLong()
        return min.minutes.inWholeMilliseconds.plus(sec.seconds.inWholeMilliseconds)
            .plus(millis.milliseconds.inWholeMilliseconds)
    }
}

fun String.toGP() = replace("Grand Prix", "GP")
