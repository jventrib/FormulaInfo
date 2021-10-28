package com.jventrib.formulainfo.result

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
import com.jventrib.formulainfo.model.db.FullResult
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.components.DeltaTextP
import com.jventrib.formulainfo.ui.components.ItemCard

@Composable
fun DriverResult(
    result: FullResult,
    onResultSelected: (result: FullResult) -> Any
) {
    ItemCard(
        image = result.driver.image,
        onItemSelected = { onResultSelected(result) },
        content = {
            Row(Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "${result.result.position}:${result.driver.givenName} ${result.driver.familyName}",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = result.constructor.name,
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = result.result.time?.time ?: "",
                        style = MaterialTheme.typography.body2
                    )
                }
                Column(horizontalAlignment = End, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${result.result.points.toInt()} pts",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(End)
                    )
                    Row() {

                        Text(
                            text = "Started ${result.result.grid}",
                            style = MaterialTheme.typography.body1
                        )
                        DeltaTextP(
                            delta = result.result.position - result.result.grid,
                            Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        shape = CircleShape,
        faceBox = Rect.unflattenFromString(result.driver.faceBox)
    )
}


@Preview
@Composable
fun DriverResultPreview() {
    val rr = FullResult(
        Result(
            "11",
            2021,
            2,
            33,
            1,
            "1",
            25.0f,
            "verstappen",
            "RedBull",
            2,
            70,
            "Finished",
            Result.Time(111, "111"),

            Result.FastestLap(
                1,
                1,
                Result.Time(111, "111"),
                Result.FastestLap.AverageSpeed("Kph", 170.0f)
            )
        ),
        Driver(
            "verstappen",
            33,
            "verst",
            "url",
            "Max",
            "Verstappen",
            "1999",
            "NL",
            "img",
            null
        ),
        Constructor("RedBull", "url", "RedBull", "UK", "img")
    )
    DriverResult(result = rr) {

    }

}