package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.theme.teamColor
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import kotlinx.coroutines.launch

@Composable
fun RaceGraphScreen(lapsByResult: Map<Result, List<Lap>>) {
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
                drivers = ResultSample.`get202101Results`(),
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
        val lapDataPoints = getDataPoints(lapsByResult, selectState)

        if (lapDataPoints.isNotEmpty()) LineGraph(
            plot = LinePlot(
                lines = lapDataPoints,
//                yAxis = LinePlot.YAxis { _, _, _ ->
//                    lapsByResult.entries
//                        .sortedByDescending { e -> e.key.resultInfo.grid.let { if (it == 0) 20 else it } }
//                        .map { it.key.driver.code!! }
//                        .forEach {
//                            Text(
//                                text = it,
//                                overflow = TextOverflow.Ellipsis,
//                                style = MaterialTheme.typography.caption,
//                                color = MaterialTheme.colors.onSurface,
//                            )
//                        }
//                },
                yAxis = LinePlot.YAxis(steps = 20),
                paddingTop = 8.dp
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getDataPoints(
    lapsByResult: Map<Result, List<Lap>>,
    selectState: SnapshotStateMap<String, Boolean>
): List<LinePlot.Line> {
    val lapDataPoints = lapsByResult
        .mapValues { entry ->
            entry.value.map { DataPoint(it.number.toFloat(), -it.position.toFloat()) }
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(0, DataPoint(0f, -entry.key.resultInfo.grid.toFloat()))
                    }
                }
        }
        .filter { selectState[it.key.driver.driverId] ?: true }
        .map {
            val c = teamColor[it.key.constructor.id]
            LinePlot.Line(
                it.value,
                LinePlot.Connection(color = c ?: Color.Black),
                LinePlot.Intersection(
                    color = c ?: Color.Black,
                    radius = 4.dp,
                    style = if (it.key.driver.numberInTeam == 1) Stroke() else Fill
                ),
                LinePlot.Highlight(color = Color.Yellow),
            )
        }
    return lapDataPoints
}

@Preview
@Composable
fun RaceGraphScreenPreview() {
    RaceGraphScreen(lapsByResult = ResultSample.getLapsPerResults())
}