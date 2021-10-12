package com.jventrib.formulainfo.race.ui.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.common.ui.components.Image
import com.jventrib.formulainfo.ui.theme.LightLightGrey

@Composable
fun ItemCard(
    image: Any?,
    topText: String,
    bottomText: String,
    onItemSelected: () -> Unit
) {
    ItemCard(image, onItemSelected, {
        Text(text = topText, style = MaterialTheme.typography.h6)
        Text(
            text = bottomText, style = MaterialTheme.typography.body2
        )
    }, CircleShape)
}

@Composable
fun ItemCard(
    image: Any?,
    onItemSelected: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit),
    shape: Shape = RectangleShape
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onItemSelected()
            },
        elevation = 4.dp,
        backgroundColor = LightLightGrey
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                image, Modifier
                    .padding(horizontal = 8.dp)
                    .size(64.dp)
                    .clip(shape)
            )
            Column(content = content)
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