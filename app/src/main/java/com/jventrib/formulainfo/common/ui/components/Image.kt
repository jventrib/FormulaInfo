package com.jventrib.formulainfo.common.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.Placeholder
import com.jventrib.formulainfo.R
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun Image(imageModel: Any?, modifier: Modifier = Modifier, placeholder: Any? = R.drawable.loading) {
    if (imageModel == null) {
        CoilImage(
            imageModel =  ImageBitmap.imageResource(R.drawable.loading),
            contentScale = ContentScale.FillWidth,
            modifier = modifier,
            placeHolder = placeholder

        )
    } else {
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

@Preview
@Composable
fun ImagePreview() {
    com.jventrib.formulainfo.common.ui.components.Image(
        imageModel = painterResource(R.drawable.sirotkin),
        modifier = Modifier.size(64.dp),
        placeholder = ImageBitmap.imageResource(R.drawable.sirotkin)
    )

}