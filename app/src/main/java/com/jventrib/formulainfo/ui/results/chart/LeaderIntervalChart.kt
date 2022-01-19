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
import com.jventrib.formulainfo.ui.common.*
import com.jventrib.formulainfo.ui.results.getLapsWithStart
import com.jventrib.formulainfo.ui.theme.teamColor
import java.time.Duration


@Composable
fun LeaderIntervalChart(lapsByResult: Map<Result, List<Lap>>) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val selectState = remember(lapsByResult) {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(lapsByResult.keys.map { it.driver.driverId to true })
        }
    }

    val lapsWithStart = getLapsWithStart(lapsByResult)

    val leaderLaps = lapsWithStart.values.flatten().filter { l -> l.position == 1 }
        .sortedBy { it.number }
    val series = lapsWithStart.map { entry ->
        Serie(
            entry.value.mapIndexed { index, lap ->
                DataPoint(
                    lap,
                    Offset(
                        lap.number.toFloat(),
                        (lap.total.toMillis() - leaderLaps[index].total.toMillis()).toFloat()
                    )
                )
            },
            teamColor[entry.key.constructor.id]!!,
            entry.key.driver.code ?: entry.key.driver.driverId
        )
    }

    Chart(
        series = series,
        boundaries = Boundaries(maxY = Duration.ofMinutes(1).toMillis().toFloat()),
        yOrientation = YOrientation.Down
    )
}


@Preview(showSystemUi = false)
@Composable
fun LeaderIntervalChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    LeaderIntervalChart(lapsByResult = lapsWithStart)
}