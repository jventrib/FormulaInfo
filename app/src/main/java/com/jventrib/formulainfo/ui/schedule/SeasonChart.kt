package com.jventrib.formulainfo.ui.schedule

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.Chart
import com.jventrib.formulainfo.ui.common.DataPoint
import com.jventrib.formulainfo.ui.common.Serie
import com.jventrib.formulainfo.ui.common.YOrientation
import com.jventrib.formulainfo.ui.results.chart.LapPositionChart
import com.jventrib.formulainfo.ui.results.chart.LapTimeChart
import com.jventrib.formulainfo.ui.results.chart.LeaderIntervalChart
import com.jventrib.formulainfo.ui.theme.teamColor
import java.time.Duration


@Composable
fun SeasonChart(results: List<Result>) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { "Formula Info" },
            )
        },
    ) {
//        val series = results.map { entry ->
//            Serie(
//                entry.value.map { lap ->
//                    DataPoint(
//                        lap,
//                        Offset(
//                            lap.number.toFloat(),
//                            lap.position.toFloat()
//                        )
//                    )
//                },
//                teamColor.getValue(entry.key.constructor.id),
//                entry.key.driver.code ?: entry.key.driver.driverId
//            )
//        }
//
//        Chart(
//            series = series, yOrientation = YOrientation.Down, gridStep = Offset(5f, 1f),
//        )

    }
}


//@Preview(showSystemUi = false)
//@Composable
//fun SeasonChartPreview() {
//    SeasonChart(race = null, lapsByResult = lapsWithStart)
//}