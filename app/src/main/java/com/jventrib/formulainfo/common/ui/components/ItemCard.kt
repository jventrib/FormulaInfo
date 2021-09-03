package com.jventrib.formulainfo.race.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.ui.theme.LightLightGrey

@Composable
fun ItemCard(
    imageUrl: String?,
    placeholder: Int,
    topText: String,
    bottomText: String,
    onItemSelected: () -> Unit
) {
    ItemCard(imageUrl, placeholder, onItemSelected)
    {
        Text(text = topText, style = MaterialTheme.typography.h6)
        Text(
            text = bottomText, style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun ItemCard(
    imageUrl: String?,
    placeholder: Int,
    onItemSelected: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
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
                imageUrl, Modifier
                    .padding(horizontal = 8.dp)
                    .size(64.dp), placeholder
            )
            Column(content = content)
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    ItemCard(
        imageUrl = "",
        placeholder = R.drawable.japan,
        topText = "Top text Top text Top text",
        bottomText = "Bottom text",
        onItemSelected = {}
    )

}