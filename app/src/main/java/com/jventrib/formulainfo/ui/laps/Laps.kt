@file:OptIn(ExperimentalFoundationApi::class)

package com.jventrib.formulainfo.ui.laps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.common.toLapTimeString
import com.jventrib.formulainfo.ui.race.DriverResult
import com.jventrib.formulainfo.ui.race.getResultSample
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import kotlin.random.Random

fun NavGraphBuilder.laps() {
    composable(
        "laps/{season}/{round}/{driver}",
        listOf(
            navArgument("season") { type = NavType.IntType },
            navArgument("round") { type = NavType.IntType },
            navArgument("driver") { type = NavType.StringType }
        )
    ) { navBackStackEntry ->
        val viewModel: LapsViewModel = hiltViewModel(navBackStackEntry)

        val season = navBackStackEntry.arguments!!.getInt("season")
        val round = navBackStackEntry.arguments!!.getInt("round")
        val driverId = navBackStackEntry.arguments!!.getString("driver")!!

        LaunchedEffect(season, round, driverId) {
            viewModel.setSeason(season)
            viewModel.setRound(round)
            viewModel.setDriverId(driverId)
        }

        val race by viewModel.race.collectAsStateWithLifecycle(null)
        val result by viewModel.result.collectAsStateWithLifecycle(null)
        val lapTimes by viewModel.laps.collectAsStateWithLifecycle(listOf())
        result?.let { Laps(race, it, lapTimes) }
    }
}

@Composable
private fun Laps(race: Race?, result: Result, laps: List<Lap>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(race?.raceInfo?.let { "${it.raceName} ${it.season}" } ?: "Formula Info")
                },
                actions = {
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
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
                            .background(
                                if (index % 2 == 1) MaterialTheme.colors.background
                                else MaterialTheme.colors.surface
                            )
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
    SampleLaps()
}

@Preview
@Composable
fun LapsDarkPreview() {
    FormulaInfoTheme(darkTheme = true) {
        SampleLaps()
    }
}

@Composable
private fun SampleLaps() {
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
    time,
    time
)
