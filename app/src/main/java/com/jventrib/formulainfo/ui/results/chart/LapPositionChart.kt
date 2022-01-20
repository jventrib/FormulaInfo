package com.jventrib.formulainfo.ui.results.chart

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.Chart
import com.jventrib.formulainfo.ui.common.DataPoint
import com.jventrib.formulainfo.ui.common.Serie
import com.jventrib.formulainfo.ui.common.YOrientation
import com.jventrib.formulainfo.ui.results.getLapsWithStart
import com.jventrib.formulainfo.ui.theme.teamColor


@Composable
fun LapPositionChart(lapsByResult: Map<Result, List<Lap>>) {
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
            entry.value.map { lap ->
                DataPoint(
                    lap,
                    Offset(
                        lap.number.toFloat(),
                        lap.position.toFloat()
                    )
                )
            },
            teamColor.getValue(entry.key.constructor.id),
            entry.key.driver.code ?: entry.key.driver.driverId
        )
    }

    Chart(
        series = series, yOrientation = YOrientation.Down,
    )
}


@Preview(showSystemUi = false)
@Composable
fun LapPositionChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LapPositionChart(lapsByResult = lapsWithStart)
}