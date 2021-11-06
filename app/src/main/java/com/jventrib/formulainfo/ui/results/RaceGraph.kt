package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.theme.teamColor
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import logcat.logcat
import kotlin.random.Random

@Composable
fun RaceGraphScreen(lapsByResult: Map<Result, List<Lap>>) {
    val lapDataPoints = lapsByResult.mapValues { entry ->
        val apply = entry.value.map { lap ->
            DataPoint(lap.number.toFloat(), lap.position.toFloat())
        }
            .toMutableList().apply {
                if (entry.key.resultInfo.grid != 0) {
                    add(0, DataPoint(0f, entry.key.resultInfo.grid.toFloat()))
                }
            }
        apply
    }.entries.sortedBy { it.key.resultInfo.grid }

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
        plot = LinePlot(
            lapDataPoints.mapIndexed { index, entry ->
                val c = teamColor[entry.key.constructor.id]
                LinePlot.Line(
                    entry.value,
                    LinePlot.Connection(color = c ?: Color.Black),
                    LinePlot.Intersection(color = c ?: Color.Black, radius = 4.dp),
                    LinePlot.Highlight(color = Color.Yellow),
                )
            },
            grid = LinePlot.Grid(Color.Red, steps = 4),
        ),
        modifier = Modifier
            .fillMaxSize()
    )
}

