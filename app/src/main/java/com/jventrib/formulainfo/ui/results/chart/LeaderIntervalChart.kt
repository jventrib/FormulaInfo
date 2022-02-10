package com.jventrib.formulainfo.ui.results.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.Boundaries
import com.jventrib.formulainfo.ui.common.Chart
import com.jventrib.formulainfo.ui.common.DataPoint
import com.jventrib.formulainfo.ui.common.Serie
import com.jventrib.formulainfo.ui.common.YOrientation
import com.jventrib.formulainfo.ui.results.getLapsWithStart
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

    val longestTime = secondLastLaps.maxOf { it.total.toMillis() }
    val leaderLaps = lapsWithStart.values.flatten().filter { l -> l.position == 1 }
        .sortedBy { it.number }

    val series = lapsWithStart.map { entry ->
        Serie(
            entry.value.mapIndexed { index, lap ->
                DataPoint(
                    lap,
                    Offset(
                        lap.number.toFloat(),
                        (
                            lap.total.toMillis() - (
                                leaderLaps.getOrNull(index)
                                    ?: lap
                                ).total.toMillis()
                            ).toFloat() / 1000f
                    )
                )
            },
            teamColor.getValue(entry.key.constructor.id),
            entry.key.driver.code ?: entry.key.driver.driverId.take(3)
        )
    }

    Chart(
        series = series,
        boundaries = Boundaries(
            maxY = (
                if (anteLastLap < leaderLaps.size)
                    ((longestTime - leaderLaps[anteLastLap].total.toMillis()).toFloat()) else 5000f
                ) / 1000f
        ),
        yOrientation = YOrientation.Down,
        gridStep = Offset(5f, 5f)
    )
}

@Preview(showSystemUi = false)
@Composable
fun LeaderIntervalChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LeaderIntervalChart(lapsByResult = lapsWithStart)
}
