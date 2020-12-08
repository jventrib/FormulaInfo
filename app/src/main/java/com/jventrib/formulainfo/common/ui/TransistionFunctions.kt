package com.jventrib.formulainfo.common.ui

import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import java.util.concurrent.TimeUnit

fun Fragment.postponeTransition(view: View, block: () -> Unit) {
    postponeEnterTransition()
    block()
    view.doOnPreDraw { startPostponedEnterTransition() }
}
