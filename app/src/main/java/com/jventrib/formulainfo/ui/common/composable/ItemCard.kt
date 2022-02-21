package com.jventrib.formulainfo.ui.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi

@Composable
fun ItemCard(
    image: @Composable RowScope.() -> Unit,
    topText: String,
    bottomText: String,
    onItemSelected: () -> Unit
) {
    ItemCard(
        image,
        onItemSelected,
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
    image: @Composable RowScope.() -> Unit,
    onItemSelected: () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable {
                onItemSelected()
            },
        elevation = 4.dp,
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            image()
            content()
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    ItemCard(
        image = {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .padding(vertical = 13.dp)
                    .width(64.dp)
                    .height(38.dp)
                    .clip(RectangleShape),
            )
        },
        topText = "Top text Top text Top text",
        bottomText = "Bottom text"
    ) {}
}
