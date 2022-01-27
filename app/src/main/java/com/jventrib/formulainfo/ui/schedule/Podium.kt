package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.results.getResultSample
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun Podium(results: List<Result>, modifier: Modifier = Modifier) {
    val h = 11.dp
    val w = 20.dp
    val result = results[0]
    Column(horizontalAlignment = CenterHorizontally, modifier = modifier.width(60.dp)) {
        Driver(result, w)
        Row {
            Driver(results[1], w)
            Box(
                modifier = Modifier
                    .width(w)
                    .height(h)
                    .background(Color.Gray)
            ) {
                Text(
                    "1",
                    color = Color.White,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Center)
                )
            }
            Driver(results[2], w)
        }
        Row {
            Box(
                modifier = Modifier
                    .width(w)
                    .height(h)
                    .background(Color.Gray)
            ) {
                Text(
                    "2",
                    color = Color.White,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Center)
                )

            }
            Box(
                modifier = Modifier
                    .width(w)
                    .height(h)
                    .background(Color.Gray)
            )
            Box(
                modifier = Modifier
                    .width(w)
                    .height(h)
                    .background(Color.Gray)
            ) {
                Text(
                    "3",
                    color = Color.White,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Center)
                )
            }
        }
    }
}

@Composable
private fun Driver(
    result: Result,
    w: Dp
) {
    Text(
        result.driver.code.toString(),
        modifier = Modifier
            .width(w)
            .clip(RoundedCornerShape(4.dp))
            .background(teamColor.getValue(result.constructor.id)),
        color = Color.White,
        fontSize = 8.sp,
        textAlign = TextAlign.Center,
    )
}

@Preview
@Composable
fun PodiumPreview() {
    Podium(
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
    )
}