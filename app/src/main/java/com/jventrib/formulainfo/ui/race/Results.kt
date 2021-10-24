package com.jventrib.formulainfo.result

import android.graphics.Rect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.ui.common.components.DeltaTextP
import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.RaceResult
import com.jventrib.formulainfo.model.db.FullRaceResult
import com.jventrib.formulainfo.ui.components.ItemCard

@Composable
fun Results(
    results: List<FullRaceResult>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    LazyColumn(contentPadding = contentPadding, modifier = modifier) {
        items(results) {
            ResultItem(it, onResultSelected = { })
        }
    }

}

@Composable
fun ResultItem(raceResult: FullRaceResult, onResultSelected: () -> Any) {
    ItemCard(
        image = raceResult.driver.image,
        onItemSelected = {

        },
        content = {
            Row(Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "${raceResult.raceResult.position}:${raceResult.driver.givenName} ${raceResult.driver.familyName}",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = raceResult.constructor.name,
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = raceResult.raceResult.time?.time ?: "",
                        style = MaterialTheme.typography.body2
                    )
                }
                Column(horizontalAlignment = End, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${raceResult.raceResult.points.toInt()} pts",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(End)
                    )
                    Row() {

                        Text(
                            text = "Started${raceResult.raceResult.grid}",
                            style = MaterialTheme.typography.body1
                        )
                        DeltaTextP(
                            delta = raceResult.raceResult.position - raceResult.raceResult.grid,
                            Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        shape = CircleShape,
        faceBox = Rect.unflattenFromString(raceResult.driver.faceBox)
    )
}


@Preview
@Composable
fun ResultItemPreview() {
    val rr = FullRaceResult(
        RaceResult(
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
            RaceResult.Time(111, "111"),

            RaceResult.FastestLap(
                1,
                1,
                RaceResult.Time(111, "111"),
                RaceResult.FastestLap.AverageSpeed("Kph", 170.0f)
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
    ResultItem(raceResult = rr) {

    }

}