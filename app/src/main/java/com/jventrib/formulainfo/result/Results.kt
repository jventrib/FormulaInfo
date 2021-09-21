package com.jventrib.formulainfo.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.race.model.db.Constructor
import com.jventrib.formulainfo.race.model.db.Driver
import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import com.jventrib.formulainfo.race.ui.components.ItemCard
import java.time.Instant

@Composable
fun Results(results: List<RaceResultFull>) {
    LazyColumn {
        items(results) {
            ResultItem(it, onResultSelected = { })
        }
    }

}

@Composable
fun ResultItem(raceResult: RaceResultFull, onResultSelected: () -> Any) {
    ItemCard(image = raceResult.driver.image,
        onItemSelected = {

        })
    {
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
            Column(horizontalAlignment = Alignment.End, modifier = Modifier) {
                Text(
                    text = "${raceResult.raceResult.points} pts",
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = raceResult.constructor.name,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Preview
@Composable
fun ResultItemPreview() {
    val rr = RaceResultFull(
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
            1,
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
            "img"
        ),
        Constructor("RedBull", "url", "RedBull", "UK", "img")
    )
    ResultItem(raceResult = rr) {

    }

}