package com.jventrib.formulainfo.race.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.R

@Composable
fun Image(url: String?, modifier: Modifier = Modifier, placeholder: Int = R.drawable.loading) {
    androidx.compose.foundation.Image(
        painter = rememberImagePainter(
            url,
            builder = { placeholder(placeholder) }),
        null,
        modifier
    )
}