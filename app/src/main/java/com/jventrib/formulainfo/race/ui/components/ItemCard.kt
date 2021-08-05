package com.jventrib.formulainfo.race.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.ui.theme.LightLightGrey

@Composable
fun ItemCard(
    imageUrl: String?,
    placeholder: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = LightLightGrey
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageUrl, Modifier
                    .padding(start = 8.dp)
                    .size(64.dp), placeholder
            )
            Column(Modifier.padding(8.dp), content = content)
        }
    }
}

@Composable
fun ItemCard(
    imageUrl: String?,
    placeholder: Int,
    topText: String,
    bottomText: String
) {
    ItemCard(imageUrl, placeholder) {
        Text(text = topText, style = MaterialTheme.typography.h6)
        Text(
            text = bottomText, style = MaterialTheme.typography.body2
        )
    }
}