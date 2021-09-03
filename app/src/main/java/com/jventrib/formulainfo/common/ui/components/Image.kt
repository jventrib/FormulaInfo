package com.jventrib.formulainfo.race.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun Image(imageModel: Any?, modifier: Modifier = Modifier) {
    if (imageModel != null) {
        CoilImage(
            imageModel = imageModel,
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
}