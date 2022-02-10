package com.jventrib.formulainfo.ui.common.composable

import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.utils.FaceCrop

@ExperimentalCoilApi
@Composable
fun Image(
    imageModel: Any?,
    modifier: Modifier = Modifier,
    faceBox: Rect? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    Image(
        painter = rememberImagePainter(
            data = imageModel,
            builder = {
                faceBox?.let { transformations(FaceCrop(it)) }
            },

        ),
        null,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@ExperimentalCoilApi
@Preview
@Composable
fun ImagePreview() {
    Image(
        imageModel = "",
        modifier = Modifier.size(64.dp),
        contentScale = ContentScale.FillWidth
    )
}
@ExperimentalCoilApi
@Preview
@Composable
fun ImagePreview_cropped() {
    Image(
        imageModel = R.drawable.vettel,
        modifier = Modifier.size(64.dp),
        faceBox = Rect.unflattenFromString("72 19 120 67"),
        contentScale = ContentScale.FillWidth
    )
}
