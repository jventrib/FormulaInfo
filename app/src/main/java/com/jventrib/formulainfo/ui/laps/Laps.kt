package com.jventrib.formulainfo.ui.laps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.results.DriverResult
import com.jventrib.formulainfo.ui.results.getResultSample
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import com.jventrib.formulainfo.ui.theme.LightLightGrey
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun Laps(race: Race?, result: Result, laps: List<Lap>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(race?.raceInfo?.let { "${it.raceName} ${it.season}" } ?: "Formula Info")
                },
                actions = {
                }
            )
        }) {
        Column {
            DriverResult(result = result, onResultSelected = {})
            LazyColumn {
                stickyHeader {
                    Surface {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .border(.5.dp, Color.Black)
                                .padding(4.dp)
                        ) {
                            Column(Modifier.weight(.3f)) {
                                Text(text = "Number", fontWeight = FontWeight.Black)
                            }
                            Column(Modifier.weight(.3f)) {
                                Text(text = "Position", fontWeight = FontWeight.Black)
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(.3f)
                            ) {
                                Text(text = "Time", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
                itemsIndexed(laps) { index, lapTime ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .border(.5.dp, Color.Black)
                            .background(if (index % 2 == 1) MaterialTheme.colors.background else MaterialTheme.colors.surface)
                            .padding(4.dp)
                    ) {
                        Column(
                            Modifier.weight(.3f)
                        ) {
                            Text(text = lapTime.number.toString())
                        }
                        Column(
                            Modifier.weight(.3f)
                        ) {
                            Text(text = lapTime.position.toString())
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(.3f)
                        ) {
                            Text(text = lapTime.time.toLapTimeString())
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LapsPreview() {
    getLaps()
}

@Preview
@Composable
fun LapsDarkPreview() {
    FormulaInfoTheme(darkTheme = true) {
        getLaps()
    }
}

@Composable
private fun getLaps() {
    var i = 0
    Laps(
        null,
        getResultSample("verstappen", 1),
        listOf(
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
            lap(++i, Random.nextLong(120000)),
        )
    )
}

private fun lap(
    number: Int,
    time: Long
) = Lap(
    2021,
    1,
    "max_verstappen",
    "VER",
    number,
    2,
    Duration.ofMillis(time),
    Duration.ofMillis(time)
)


private fun Duration.toLapTimeString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss.SSS")
    return LocalTime.ofNanoOfDay(this.toNanos()).format(formatter)
}
