package com.jventrib.formulainfo.common.ui

import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment

fun Fragment.beforeTransition(view: View, block: () -> Unit) {
    postponeEnterTransition()
    block()
    view.doOnPreDraw { startPostponedEnterTransition() }
}
