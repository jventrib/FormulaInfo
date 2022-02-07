package com.jventrib.formulainfo.ui.theme

import androidx.compose.ui.graphics.Color

val Red = Color(0xFFBD0D01)
val RedDark = Color(0xFF812019)
val Purple = Color(0xFF3F51B5)
val LightLightGrey = Color(0xFFEEEEEE)
val DarkDarkGrey = Color(0xFF111111)

val teamColor = mapOf(
    "mercedes" to Color(0xFF00D2BE),
    "ferrari" to Color(0xFFDC0000),
    "mclaren" to Color(0xFFFF8700),
    "aston_martin" to Color(0xFF006F62),
    "alphatauri" to Color(0xFF2B4562),
    "alpine" to Color(0xFF0190FF),
    "alfa" to Color(0xFF960000),
    "williams" to Color(0xFF0082FA),
    "red_bull" to Color(0xFF0600EF),
    "haas" to Color(0xFF787878)
).withDefault { Color.Gray }
