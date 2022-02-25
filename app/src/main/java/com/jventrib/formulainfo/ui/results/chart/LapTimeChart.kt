package com.jventrib.formulainfo.ui.results.chart

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
import com.jventrib.formulainfo.ui.common.toLapTimeString
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun LapTimeChart(lapsByResult: Map<Result, List<Lap>>) {
//    val scaffoldState = rememberScaffoldState()
//    val scope = rememberCoroutineScope()
//    val selectState = remember(lapsByResult) {
//        mutableStateMapOf<String, Boolean>().apply {
//            putAll(lapsByResult.keys.map { it.driver.driverId to true })
//        }
//    }

    val series = lapsByResult.map { entry ->
        Serie(
            entry.value.map { lap ->
                DataPoint(
                    lap,
                    Offset(
                        lap.number.toFloat(),
                        lap.time.toFloat() / 1000,
                    )
                )
            },
            teamColor.getValue(entry.key.constructor.id),
            entry.key.driver.code ?: entry.key.driver.driverId.take(3)
        )
    }

    Chart(
        series = series,
        yOrientation = YOrientation.Up,
        gridStep = Offset(5f, 1f),
        yLabelTransform = { (it * 1000).toLong().toLapTimeString("mm:ss") }
    )
}

@Preview(showSystemUi = false)
@Composable
fun LapTimeChartPreview() {
    val lapsWithStart = ResultSample.getLapsPerResults()
    LapTimeChart(lapsByResult = lapsWithStart)
}
