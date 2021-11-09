package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
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
import java.io.File
import kotlin.random.Random

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
        drawerContent = { DriverSelector(drivers = ResultSample.`get202101Results`(), selectState) },
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
        val lapDataPoints = lapsByResult.mapValues { entry ->
            entry.value.map { lap ->
                DataPoint(lap.number.toFloat(), lap.position.toFloat())
            }
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(0, DataPoint(0f, entry.key.resultInfo.grid.toFloat()))
                    }
                }
        }.entries.sortedBy { it.key.resultInfo.grid }
            .filter { selectState[it.key.driver.driverId] ?: true }
            .mapIndexed { index, entry ->
                val c = teamColor[entry.key.constructor.id]
                LinePlot.Line(
                    entry.value,
                    LinePlot.Connection(color = c ?: Color.Black),
                    LinePlot.Intersection(
                        color = c ?: Color.Black,
                        radius = 4.dp,
                        style = if (entry.key.driver.numberInTeam == 1) Stroke() else Fill
                    ),
                    LinePlot.Highlight(color = Color.Yellow),
                )
            }

        val colors by remember {
            mutableStateOf((0..20).map {
                it to Color(
                    Random.nextInt(255),
                    Random.nextInt(255),
                    Random.nextInt(255)
                )
            }.toMap())
        }


        if (lapDataPoints.isNotEmpty()) LineGraph(
            plot = LinePlot(lapDataPoints, grid = LinePlot.Grid(Color.Red, steps = 4)),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun RaceGraphScreenPreview() {
    RaceGraphScreen(lapsByResult = ResultSample.getLapsPerResults() )
//    val resource = ResultSample.get202101Results()
//    Text(resource.toString())
}