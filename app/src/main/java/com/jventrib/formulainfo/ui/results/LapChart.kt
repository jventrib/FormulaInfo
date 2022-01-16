package com.jventrib.formulainfo.ui.results

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import java.time.Duration


@Composable
fun LapChart(lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var selectedChart by remember { mutableStateOf(Charts.Position) }
    val selectState = remember(lapsByResult) {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(lapsByResult.keys.map { it.driver.driverId to true })
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Formula Info") },
                actions = {
                    LapChartMenu(selectedChart.label) { selectedChart = it }
                }
            )
        },
    ) {
        when (selectedChart) {
            Charts.Time -> LapTimeChart(lapsByResult = lapsByResult)
            Charts.Position -> LapPositionChart(lapsByResult = lapsByResult)
        }
    }
}

enum class Charts(val label: String) {
    Position("Position by lap"), Time("Time by lap")

}

internal fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>): Map<Result, List<Lap>> =
    lapsByResult
        .mapValues { entry ->
            entry.value
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(
                            0, Lap(
                                season = entry.key.resultInfo.season,
                                round = entry.key.resultInfo.round,
                                driverId = entry.key.driver.driverId,
                                driverCode = entry.key.driver.code ?: entry.key.driver.driverId,
                                number = 0,
                                position = entry.key.resultInfo.grid,
                                time = Duration.ZERO,
                                total = Duration.ZERO
                            )
                        )
                    }
                }
        }

@Preview(showSystemUi = false)
@Composable
fun LapChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LapChart(lapsByResult = lapsWithStart)
}