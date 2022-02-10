package com.jventrib.formulainfo.ui.common

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

operator fun Offset.div(other: Offset): Offset {
    return Offset(this.x / other.x, this.y / other.y)
}

operator fun Offset.rem(other: Offset): Offset {
    return Offset(this.x % other.x, this.y % other.y)
}

operator fun Offset.times(other: Offset): Offset {
    return Offset(this.x * other.x, this.y * other.y)
}

fun Offset.coerceAtLeast(min: Offset): Offset = this.let {
    Offset(it.x.coerceAtLeast(min.x), it.y.coerceAtLeast(min.y))
}

fun Offset.coerceAtMost(max: Offset): Offset = this.let {
    Offset(it.x.coerceAtMost(max.x), it.y.coerceAtMost(max.y))
}

fun Offset.coerceIn(min: Offset, max: Offset): Offset = this.let {
    Offset(it.x.coerceIn(min.x, max.x), it.y.coerceIn(min.y, max.y))
}

fun abs(offset: Offset): Offset = offset.let { return Offset(abs(it.x), abs(it.y)) }
