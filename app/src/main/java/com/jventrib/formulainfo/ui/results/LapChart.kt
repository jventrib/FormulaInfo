package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.theme.teamColor
import kotlinx.coroutines.launch
import java.time.Duration


@Composable
fun LapChart(lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val selectState = remember(lapsByResult) {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(lapsByResult.keys.map { it.driver.driverId to true })
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerShape = customShape(),
        drawerContent = {
            DriverSelector(
                drivers = ResultSample.get202101Results(),
                selectState
            )
        },
        topBar = {
            TopAppBar(
                title = { Text("Formula Info") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch { scaffoldState.drawerState.open() }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
    ) {

        val lapsWithStart = getLapsWithStart(lapsByResult)

        val series = lapsWithStart.map { entry ->
            Serie(
                entry.value.map { lap ->
                    DataPoint(
                        lap.number.toFloat(),
                        lap.position.toFloat(),
                        lap
                    )
                },
                teamColor[entry.key.constructor.id]!!,
                entry.key.driver.code ?: entry.key.driver.driverId
            )
        }

        Chart(series = series)
    }
}

private fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>): Map<Result, List<Lap>> =
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
    val series = lapsWithStart.map { entry ->
        Serie(
            entry.value.map { lap ->
                DataPoint(
                    lap.number.toFloat(),
                    lap.position.toFloat(),
                    lap
                )
            },
            teamColor[entry.key.constructor.id]!!,
            entry.key.driver.code ?: entry.key.driver.driverId
        )
    }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(3.dp, Color.Blue)
    ) {
        Chart(
            series = series,
            modifier = Modifier
                .fillMaxHeight(1f)
                .border(2.dp, Color.Red),
            boundary = Boundary(maxY = 20f))
    }
//    Chart(map, maxYValue = lapsWithStart.size.toFloat())
}