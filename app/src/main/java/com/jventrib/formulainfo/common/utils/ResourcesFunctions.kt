package com.jventrib.formulainfo.common.utils

import android.content.Context

fun Context.getLong(resId: Int) = resources.getInteger(resId).toLong()