package com.jventrib.formulainfo.ui.results.chart

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import androidx.core.graphics.translationMatrix
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.*
import com.jventrib.formulainfo.ui.results.getLapsWithStart
import com.jventrib.formulainfo.ui.theme.teamColor


@Composable
fun MeanIntervalChart(lapsByResult: Map<Result, List<Lap>>) {
    if (lapsByResult.isEmpty()) return
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val selectState = remember(lapsByResult) {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(lapsByResult.keys.map { it.driver.driverId to true })
        }
    }

    val lapsWithStart = getLapsWithStart(lapsByResult)


    val series = lapsWithStart.mapNotNull { entry ->
        if (entry.value.size > 1) {
            Serie(
                entry.value.mapIndexed { index, lap ->
                    DataPoint(
                        lap,
                        Offset(
                            lap.number.toFloat(),
                            lap.time.toMillis().toFloat()
                        )
                    )
                },
                teamColor.getValue(entry.key.constructor.id),
                entry.key.driver.code ?: entry.key.driver.driverId
            )
        } else null
    }


    val average = lapsWithStart.values.flatten().map { it.time.toMillis() }.average().toFloat()
    Chart(
        series = series,
        yOrientation = YOrientation.Up,
        boundaries = Boundaries(minY = average * .8f, maxY = average * 1.2f),
        gridStep = Offset(10f, 60000f)
    )
}


@Preview(showSystemUi = false)
@Composable
fun MeanIntervalChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    MeanIntervalChart(lapsByResult = lapsWithStart)
}