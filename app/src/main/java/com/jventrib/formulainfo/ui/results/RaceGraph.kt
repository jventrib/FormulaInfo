package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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
import java.time.Duration

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
        val lapDataPoints = getDataPoints(lapsByResult, selectState)

        if (lapDataPoints.isNotEmpty()) LineGraph(
            plot = LinePlot(
                lines = lapDataPoints,
                yAxis = LinePlot.YAxis(steps = 20) { _, _, _ ->
                    DriverYAxis(lapsWithStart = getLapsWithStart(lapsByResult), lap = 0)
                },
                paddingTop = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getDataPoints(
    lapsByResult: Map<Result, List<Lap>>,
    selectState: SnapshotStateMap<String, Boolean>
): List<LinePlot.Line> {
    val lapDataPoints = getLapsWithStart(lapsByResult)
        .filter { selectState[it.key.driver.driverId] ?: true }
        .map { entry ->
            val c = teamColor[entry.key.constructor.id]
            LinePlot.Line(
                entry.value.map { DataPoint(it.number.toFloat(), -it.position.toFloat()) },
                LinePlot.Connection(color = c ?: Color.Black),
                LinePlot.Intersection(
                    color = c ?: Color.Black,
                    radius = 4.dp,
                    style = if (entry.key.driver.numberInTeam == 1) Stroke() else Fill
                ),
                LinePlot.Highlight(color = Color.Yellow),
            )
        }
    return lapDataPoints
}

private fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>) =
    lapsByResult
        .mapValues { entry ->
            entry.value
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(
                            0, Lap(
                                entry.key.resultInfo.season,
                                entry.key.resultInfo.round,
                                entry.key.driver.driverId,
                                entry.key.driver.code ?: entry.key.driver.driverId,
                                0,
                                entry.key.resultInfo.grid,
                                Duration.ZERO,
                                Duration.ZERO
                            )
                        )
                    }
                }
        }

@Composable
private fun DriverYAxis(
    lapsWithStart: Map<Result, MutableList<Lap>>,
    lap: Int
) {
    BoxWithConstraints(
        Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .border(1.dp, Color.Red)
    ) {
        val results = lapsWithStart
            .map { entry ->
                entry.value.firstOrNull { it.number == lap }
            }
        results.map { entry ->
            entry?.let {
                val lerp =
                    lerp(0.dp, this.maxHeight, (it.position - 1).toFloat() / results.size.toFloat())
                Text(text = it.driverCode, modifier = Modifier.offset(y = lerp))
            }
        }
    }
}

@Preview
@Composable
fun RaceGraphScreenPreview() {
    RaceGraphScreen(lapsByResult = ResultSample.getLapsPerResults())
}

//@Preview
//@Composable
//fun DriversYAxisPreview() {
//    val lap = 0
//
//    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
//    Box {
//        Image(painter = ColorPainter(Color.Red), contentDescription = "",  modifier = Modifier
//            .align(Alignment.Center)
//            .fillMaxHeight()
//            .fillMaxWidth()
//            .offset(x= 30.dp)
//        )
//        Image(painter = ColorPainter(Color.Blue.copy(alpha = 0.5f)), contentDescription = "",  modifier = Modifier
//            .fillMaxHeight()
//            .width(50.dp)
//        )
////        DriverYAxis(lapsWithStart, lap)
//    }
//}
