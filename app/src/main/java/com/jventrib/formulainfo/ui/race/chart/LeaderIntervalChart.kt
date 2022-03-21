package com.jventrib.formulainfo.ui.race.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.Chart
import com.jventrib.formulainfo.ui.common.composable.DataPoint
import com.jventrib.formulainfo.ui.common.composable.Serie
import com.jventrib.formulainfo.ui.common.composable.YOrientation
import com.jventrib.formulainfo.ui.race.getLapsWithStart
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun LeaderIntervalChart(lapsByResult: Map<Result, List<Lap>>) {
    if (lapsByResult.isEmpty()) return
//    val scaffoldState = rememberScaffoldState()
//    val scope = rememberCoroutineScope()
//    val selectState = remember(lapsByResult) {
//        mutableStateMapOf<String, Boolean>().apply {
//            putAll(lapsByResult.keys.map { it.driver.driverId to true })
//        }
//    }

    val lapsWithStart = getLapsWithStart(lapsByResult)

    val anteLastLap = (lapsWithStart.keys.maxOf { it.resultInfo.laps } - 2).coerceAtLeast(0)
    val secondLastLaps = lapsWithStart.values
        .mapNotNull { it.getOrNull(anteLastLap) }

    val longestTime = secondLastLaps.maxOf { it.total }
    val leaderLaps = lapsWithStart.values.flatten()
        .groupBy(Lap::number).mapValues { it.value.minByOrNull(Lap::position) }
        .values
        .filterNotNull()
        .sortedBy { it.number }

    val series = lapsWithStart.map { entry ->
        Serie(
            entry.value.mapIndexed { index, lap ->
                DataPoint(
                    lap,
                    Offset(
                        lap.number.toFloat(),
                        (lap.total - (leaderLaps.getOrNull(index) ?: lap).total).toFloat() / 1000f
                    )
                )
            },
            teamColor.getValue(entry.key.constructor.id),
            entry.key.driver.code ?: entry.key.driver.driverId.take(3)
        )
    }

    Chart(
        series = series,
        modifier = Modifier.semantics {
            testTag = "chart"
        },
        yOrientation = YOrientation.Down,
        gridStep = Offset(5f, 5f),
    )
}

@Preview(showSystemUi = false)
@Composable
fun LeaderIntervalChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LeaderIntervalChart(lapsByResult = lapsWithStart)
}
