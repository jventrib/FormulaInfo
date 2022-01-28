package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
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
fun DriverStandingChart(
    season: Int,
    standings: Map<Driver, List<DriverStanding>>
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$season standing",
                        modifier = Modifier.clickable {})
                },
            )
        }) {

//) {
        val series = standings.map { entry ->
            Serie(
                entry.value.mapIndexed { index, round ->
                    DataPoint(
                        round,
                        Offset(
                            round.round!!.toFloat(),
                            round.points
                        )
                    )
                },
                teamColor.getValue(entry.value.first().constructor.id),
                entry.key.code ?: entry.key.driverId.take(3)
            )
        }

        Chart(
            series = series, yOrientation = YOrientation.Up, gridStep = Offset(5f, 10f)
        )

    }
}


//@Preview(showSystemUi = false)
//@Composable
//fun SeasonChartPreview() {
//    SeasonChart(race = null, lapsByResult = lapsWithStart)
//}