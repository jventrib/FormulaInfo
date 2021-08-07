package com.jventrib.formulainfo.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.race.ui.list.SeasonMenu

@Composable
fun About() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formula Info") },
            )
        }) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(
                        R.mipmap.ic_launcher_round,
                        builder = { this.placeholder(R.mipmap.ic_launcher_round) }),
                    null
                )
                Column() {
                    Text(
                        text = stringResource(R.string.about_title),
                        style = MaterialTheme.typography.h4
                    )
                    Text(
                        text = "0.0.1",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.align(End)
                    )

                }
            }
            B2(R.string.about_developed)
            Text(
                text = with(AnnotatedString.Builder()) {
                    append("link: Jetpack Compose")
                    // attach a string annotation that stores a URL to the text "Jetpack Compose".
                    addStringAnnotation(
                        tag = "URL",
                        annotation = "https://developer.android.com/jetpack/compose",
                        start = 6,
                        end = 21
                    )
                    toAnnotatedString()
                },
                style = MaterialTheme.typography.body2
            )
            B2(R.string.about_purpose)
            H6(R.string.about_disclaimer_title)
            B2(R.string.about_disclaimer)
            H6(R.string.about_datasource_title)

            Row {
                S2(R.string.about_datasource_ergast_title)
                B2(R.string.about_datasource_ergast)
            }
            Row {
                S2(R.string.about_datasource_wikipedia_title)
                B2(R.string.about_datasource_wikipedia)
            }
            Row {
                S2(R.string.about_datasource_f1c_title)
                B2(R.string.about_datasource_f1c)
            }
        }
    }
}

@Composable
private fun H6(resId: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(resId),
        style = MaterialTheme.typography.h6,
        modifier = modifier.padding(top = 16.dp)
    )
}

@Preview
@Composable
fun AboutPreview() {
    About()
}

@Composable
private fun B2(resId: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(resId),
        style = MaterialTheme.typography.body2,
        modifier = modifier.padding(top = 16.dp)
    )
}

@Composable
private fun RowScope.S2(resId: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(resId),
        style = MaterialTheme.typography.subtitle2,
        modifier = modifier
            .padding(top = 16.dp, end = 8.dp)
            .weight(.3f)
    )
}

@Composable
private fun RowScope.B2(resId: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(resId),
        style = MaterialTheme.typography.body2,
        modifier = modifier
            .padding(top = 16.dp)
            .weight(.7f)
    )
}
