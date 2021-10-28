package com.jventrib.formulainfo.ui.laps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.LapTime
import com.jventrib.formulainfo.result.DriverResult
import com.jventrib.formulainfo.result.getResultSample
import com.jventrib.formulainfo.ui.components.ItemCard
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun Laps(result: Result, laps: List<LapTime>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable {})
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
                                Text(text = "Number")
                            }
                            Column(Modifier.weight(.3f)) {
                                Text(text = "Position")
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(.3f)
                            ) {
                                Text(text = "Time")
                            }
                        }
                    }
                }
                items(laps) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .border(.5.dp, Color.Black)
                            .padding(4.dp)
                    ) {
                        Column(
                            Modifier.weight(.3f)
                        ) {
                            Text(text = it.number.toString())
                        }
                        Column(
                            Modifier.weight(.3f)
                        ) {
                            Text(text = it.position.toString())
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(.3f)
                        ) {
                            Text(text = it.time.toLapTimeString())
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
    var i = 0
    Laps(
        getResultSample(),
        listOf(
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
            lapTime(++i, Random.nextLong(120000)),
        )
    )

}

@Composable
private fun lapTime(
    number: Int,
    time: Long
) = LapTime(
    "key",
    2021,
    1,
    "max_verstappen",
    number,
    2,
    Duration.ofMillis(time)
)


private fun Duration.toLapTimeString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss.SSS")
    return LocalTime.ofNanoOfDay(this.toNanos()).format(formatter)
}
