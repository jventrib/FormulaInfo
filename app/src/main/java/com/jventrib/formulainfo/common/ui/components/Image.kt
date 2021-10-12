package com.jventrib.formulainfo.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.commit451.coiltransformations.facedetection.CenterOnFaceTransformation
import com.jventrib.formulainfo.R

@Composable
fun Image(imageModel: Any?, modifier: Modifier = Modifier, centerOnFace: Boolean = false) {
    Image(
        painter = rememberImagePainter(
            imageModel,
            builder = { if (centerOnFace) {transformations(CenterOnFaceTransformation(zoom = 100))} }
        ),
        null,
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
    )
}

@Preview
@Composable
fun ImagePreview() {
    Image(
        imageModel = painterResource(R.drawable.sirotkin),
        modifier = Modifier.size(64.dp)
    )

}