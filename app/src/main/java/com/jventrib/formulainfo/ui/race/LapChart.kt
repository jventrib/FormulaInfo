package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.aggregate.DriverAndConstructor
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.TopAppBarMenu
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.common.toGP
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.drivers.driverSelectionSaver
import com.jventrib.formulainfo.ui.race.chart.LapPositionChart
import com.jventrib.formulainfo.ui.race.chart.LapTimeChart
import com.jventrib.formulainfo.ui.race.chart.LeaderIntervalChart
import com.jventrib.formulainfo.ui.schedule.getRaceSample

fun NavGraphBuilder.lapChart() {
    composable(
        "resultsGraph/{season}/{round}",
        listOf(
            navArgument("season") { type = NavType.IntType },
            navArgument("round") { type = NavType.IntType }
        )
    ) { navBackStackEntry ->
        val viewModel: RaceViewModel = hiltViewModel(navBackStackEntry)

        val season = navBackStackEntry.arguments?.get("season") as Int
        val round = navBackStackEntry.arguments?.get("round") as Int

        LaunchedEffect(season, round) {
            viewModel.setSeason(season)
            viewModel.setRound(round)
        }

        val race by viewModel.race.collectAsStateWithLifecycle(null)
        val resultsWithLaps by viewModel.resultsWithLaps.collectAsStateWithLifecycle(mapOf())

        if (resultsWithLaps.isNotEmpty()) {
            LapChart(race, lapsByResult = resultsWithLaps)
        }
    }
}

@Composable
private fun LapChart(race: Race?, lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    var selectedChart by rememberSaveable { mutableStateOf(Charts.values().first()) }
    val selectedDrivers = rememberSaveable(lapsByResult, saver = driverSelectionSaver) {
        lapsByResult.keys.map { it.driver.driverId to true }.toMutableStateMap()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBarMenu(
                title = {
                    Text(
                        race?.raceInfo?.let { "${it.raceName.toGP()} ${it.season}" }
                            ?: "Formula Info"
                    )
                },
                actions = {
                    LapChartMenu(selectedChart.label) { selectedChart = it }
                },
                scaffoldState = scaffoldState
            )
        },
        drawerShape = customShape(),
        drawerContent = {
            DriverSelector(
                drivers = lapsByResult.keys.toList().sortedBy { it.resultInfo.position }
                    .map { DriverAndConstructor(it.driver, it.constructor) },
                selectedDrivers
            )
        },

    ) {
        Box(modifier = Modifier.padding(it)) {
            selectedChart.compose(
                lapsByResult.filter {
                    selectedDrivers[it.key.driver.driverId] ?: false
                }
            )
        }
    }
}

enum class Charts(val label: String, val compose: @Composable (Map<Result, List<Lap>>) -> Unit) {
    LeaderInterval("Leader Interval", { LeaderIntervalChart(it) }),
    Position("Position by lap", { LapPositionChart(it) }),
    Time("Time by lap", { LapTimeChart(it) }),
}

internal fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>): Map<Result, List<Lap>> =
    lapsByResult
        .mapValues { entry ->
            entry.value
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(
                            0,
                            Lap(
                                season = entry.key.resultInfo.season,
                                round = entry.key.resultInfo.round,
                                driverId = entry.key.driver.driverId,
                                driverCode = entry.key.driver.code ?: entry.key.driver.driverId,
                                number = 0,
                                position = entry.key.resultInfo.grid,
                                time = 0,
                                total = 0
                            )
                        )
                    }
                }
        }

fun getDriversIndices(keys: Set<Result>) =
    keys.groupBy { it.constructor }.values.flatMap { results ->
        results.sortedBy { it.driver.driverId }.withIndex()
    }.associate {
        it.value.driver.driverId to it.index
    }

@Preview(showSystemUi = false)
@Composable
fun LapChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LapChart(race = getRaceSample(1, "Barhain Grand Prix"), lapsByResult = lapsWithStart)
}
