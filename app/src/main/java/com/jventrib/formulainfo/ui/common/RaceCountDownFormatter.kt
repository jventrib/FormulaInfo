package com.jventrib.formulainfo.ui.common

import android.content.res.Resources
import com.jventrib.formulainfo.R

fun raceCountDownFormat(
    resources: Resources,
    days: Long,
    hours: Int,
    minutes: Int,
    seconds: Int
): String {

    val time = "$hours:${minutes.toString().padStart(2, '0')}" +
        ":${seconds.toString().padStart(2, '0')}"

    return if (days.toInt() == 0) time else resources.getQuantityString(
        R.plurals.race_countdown_format,
        days.toInt(),
        days,
        time
    )
}
