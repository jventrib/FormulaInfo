package com.jventrib.formulainfo.ui.results

import android.graphics.Rect
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.ui.common.composable.DeltaTextP
import com.jventrib.formulainfo.ui.components.ItemCard

@Composable
fun DriverResult(
    result: Result,
    onResultSelected: (result: Result) -> Any
) {
    ItemCard(
        image = result.driver.image,
        onItemSelected = { onResultSelected(result) },
        shape = CircleShape,
        faceBox = Rect.unflattenFromString(result.driver.faceBox)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "${result.resultInfo.position}:${result.driver.givenName} ${result.driver.familyName}",
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
    Constructor("RedBull", "url", "RedBull", "UK", "img")
)
