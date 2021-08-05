package com.jventrib.formulainfo.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.R

@Composable
@Preview
fun About() {
    Column(Modifier.fillMaxWidth()) {
        Row {
            androidx.compose.foundation.Image(
                painter = rememberImagePainter("", builder = {this.placeholder(R.mipmap.ic_launcher_round)}),
                null
            )
            Text(text = "Formula Info")
        }

    }

}
