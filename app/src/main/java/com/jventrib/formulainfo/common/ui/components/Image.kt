package com.jventrib.formulainfo.race.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.R
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun Image(url: String, modifier: Modifier = Modifier, placeholder: Int = R.drawable.loading) {
    CoilImage(
        imageModel = url,
        contentScale = ContentScale.FillWidth,
        modifier = modifier,
        shimmerParams = ShimmerParams(
            baseColor = MaterialTheme.colors.background,
            highlightColor = Color.White,
            durationMillis = 350,
            dropOff = 0.65f,
            tilt = 20f
        )
    )
}