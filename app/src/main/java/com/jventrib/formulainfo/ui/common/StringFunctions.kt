package com.jventrib.formulainfo.ui.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

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
