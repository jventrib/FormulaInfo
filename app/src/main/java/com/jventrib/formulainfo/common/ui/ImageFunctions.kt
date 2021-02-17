package com.jventrib.formulainfo.common.ui

import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest

fun ImageView.loadImage(it: String, builder: ImageRequest.Builder.() -> Unit = {}) {
    val request = ImageRequest.Builder(this.context)
        .data(it)
        .target { setImageDrawable(it) }
        .apply(builder)
        .build()
    context.imageLoader.enqueue(request)
}
