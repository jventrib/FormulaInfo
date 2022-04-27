package com.jventrib.formulainfo.ui.race

import android.graphics.Rect
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.ui.common.composable.DeltaTextP
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.common.composable.ItemCard
import com.jventrib.formulainfo.ui.theme.color

@Composable
fun DriverResult(
    result: Result,
    onResultSelected: (result: Result) -> Any
) {
    ItemCard(
        border = result.constructor.color,
        image = {
            Image(
                imageModel = result.driver.image,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
                faceBox = Rect.unflattenFromString(result.driver.faceBox)
            )
        },
        onItemSelected = { onResultSelected(result) },
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "${result.resultInfo.position}:${result.driver.givenName} " +
                        result.driver.familyName,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = result.constructor.name,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = result.resultInfo.time?.time ?: "",
                    style = MaterialTheme.typography.body2
                )
            }
            Column(horizontalAlignment = End, modifier = Modifier.fillMaxWidth()) {
                if (result.resultInfo.session != Session.QUAL) {
                    Text(
                        text = "${result.resultInfo.points.toInt()} pts",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(End)
                    )
                    Row {

                        Text(
                            text = "Started ${result.resultInfo.grid}",
                            style = MaterialTheme.typography.body1
                        )
                        DeltaTextP(
                            delta = result.resultInfo.position - result.resultInfo.grid,
                            Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DriverResultPreview() {
    val rr = getResultSample("verstappen", 1)
    DriverResult(result = rr) {
    }
}

fun getResultSample(driver: String, position: Int) = Result(
    ResultInfo(
        "11",
        2021,
        2,
        Session.RACE,
        33,
        position,
        "1",
        25.0f,
        driver,
        "RedBull",
        2,
        70,
        "Finished",
        ResultInfo.Time(111, "111"),

        ResultInfo.FastestLap(
            position,
            position,
            ResultInfo.Time(111, "111"),
            ResultInfo.FastestLap.AverageSpeed("Kph", 170.0f)
        )
    ),
    Driver(
        driver,
        33,
        driver.uppercase().substring(0, 3),
        "url",
        "John",
        driver,
        "1999",
        "NL",
        "img",
        null,
        1
    ),
    Constructor("red_bull", "url", "RedBull", "UK", "img")
)
