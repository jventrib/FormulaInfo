package com.jventrib.formulainfo.common.ui

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(it: String) {
    Glide.with(this.context).load(it).into(this)
}
