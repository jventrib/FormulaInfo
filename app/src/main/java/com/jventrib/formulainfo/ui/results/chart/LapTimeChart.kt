package com.jventrib.formulainfo.ui.results

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.Chart
import com.jventrib.formulainfo.ui.common.DataPoint
import com.jventrib.formulainfo.ui.common.Serie
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun LapTimeChart(lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val selectState = remember(lapsByResult) {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(lapsByResult.keys.map { it.driver.driverId to true })
        }
    }

    val lapsWithStart = getLapsWithStart(lapsByResult)

    val series = lapsWithStart.map { entry ->
        Serie(
            entry.value.mapIndexed { index, lap ->
                DataPoint(
                    lap.total.toMillis().toFloat(),
                    entry.key.resultInfo.position.toFloat(),
                    lap
                )
            },
            teamColor[entry.key.constructor.id]!!,
            entry.key.driver.code ?: entry.key.driver.driverId
        )
    }

    Chart(series = series) { series ->
        val numbers = series.flatMap {
            it.seriePoints
        }.groupBy { it.element?.number }.map { it.value }
        numbers.forEach { number ->
            drawPoints(
                number.sortedBy { it.y }.map { it.offset }, PointMode.Polygon,
                Color.Gray,
                1.dp.toPx(),
                StrokeCap.Round,
            )

        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun LapTimeChartPreview() {
    val lapsWithStart = ResultSample.getLapsPerResults()
    LapTimeChart(lapsByResult = lapsWithStart)
}