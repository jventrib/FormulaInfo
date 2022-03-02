package com.jventrib.formulainfo.ui.results

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.aggregate.DriverAndConstructor
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.drivers.driverSelectionSaver
import com.jventrib.formulainfo.ui.results.chart.LapPositionChart
import com.jventrib.formulainfo.ui.results.chart.LapTimeChart
import com.jventrib.formulainfo.ui.results.chart.LeaderIntervalChart

@Composable
fun LapChart(race: Race?, lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    var selectedChart by rememberSaveable { mutableStateOf(Charts.values().first()) }
    val pairs = lapsByResult.keys.map { it.driver.driverId to true }.toTypedArray()
    val selectedDrivers = rememberSaveable(lapsByResult, saver = driverSelectionSaver) {
        mutableStateMapOf(*pairs)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(race?.raceInfo?.let { "${it.raceName} ${it.season}" } ?: "Formula Info")
                },
                actions = {
                    LapChartMenu(selectedChart.label) { selectedChart = it }
                }
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
        selectedChart.compose(
            lapsByResult.filter {
                selectedDrivers[it.key.driver.driverId] ?: false
            }
        )
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

@Preview(showSystemUi = false)
@Composable
fun LapChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LapChart(race = null, lapsByResult = lapsWithStart)
}
