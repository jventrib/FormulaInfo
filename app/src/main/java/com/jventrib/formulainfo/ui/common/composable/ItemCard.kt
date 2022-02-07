package com.jventrib.formulainfo.ui.components

import android.graphics.Rect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.ui.common.composable.Image

@Composable
fun ItemCard(
    image: Any?,
    topText: String,
    bottomText: String,
    onItemSelected: () -> Unit
) {
    ItemCard(
        image,
        onItemSelected,
        CircleShape,
    ) {
        Column(Modifier.padding(bottom = 4.dp)) {
            Text(text = topText, style = MaterialTheme.typography.h6)
            Text(
                text = bottomText, style = MaterialTheme.typography.body2
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ItemCard(
    image: Any?,
    onItemSelected: () -> Unit = {},
    shape: Shape = RectangleShape,
    faceBox: Rect? = null,
    content: @Composable (() -> Unit)
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onItemSelected()
            },
        elevation = 4.dp,
//        backgroundColor = LightLightGrey
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageModel = image,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(64.dp)
                    .clip(shape),
                faceBox = faceBox,
                contentScale = ContentScale.FillWidth
            )
            content()
//            Column(
//                Modifier.padding(bottom = 4.dp),
//                content = content
//            )
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    ItemCard(
        image = "",
        topText = "Top text Top text Top text",
        bottomText = "Bottom text"
    ) {}

}