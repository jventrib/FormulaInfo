package com.jventrib.formulainfo.ui.race.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.Chart
import com.jventrib.formulainfo.ui.common.composable.DataPoint
import com.jventrib.formulainfo.ui.common.composable.Serie
import com.jventrib.formulainfo.ui.common.composable.YOrientation
import com.jventrib.formulainfo.ui.race.getLapsWithStart
import com.jventrib.formulainfo.ui.theme.color

@Composable
fun LapPositionChart(lapsByResult: Map<Result, List<Lap>>) {
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
            entry.key.constructor.color,
            entry.key.driver.code ?: entry.key.driver.driverId.take(3)
        )
    }

    Chart(
        series = series, yOrientation = YOrientation.Down, gridStep = Offset(5f, 1f)
    )
}

@Preview(showSystemUi = false)
@Composable
fun LapPositionChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LapPositionChart(lapsByResult = lapsWithStart)
}
