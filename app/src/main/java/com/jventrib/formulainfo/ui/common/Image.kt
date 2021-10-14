package com.jventrib.formulainfo.ui.common.components

import android.graphics.Rect
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.commit451.coiltransformations.facedetection.CenterOnFaceTransformation
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.utils.FaceCrop

@ExperimentalCoilApi
@Composable
fun Image(imageModel: Any?, modifier: Modifier = Modifier, faceBox: Rect? = null) {
    Image(
        painter = rememberImagePainter(
            imageModel,
            builder = {
                faceBox?.let { transformations(FaceCrop(it)) }
            }
        ),
        null,
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
    )
}

@ExperimentalCoilApi
@Preview
@Composable
fun ImagePreview() {
    Image(
        imageModel = painterResource(R.drawable.sirotkin),
        modifier = Modifier.size(64.dp)
    )

}