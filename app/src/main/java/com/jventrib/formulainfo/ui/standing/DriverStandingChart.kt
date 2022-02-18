package com.jventrib.formulainfo.ui.standing

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.ui.common.composable.Chart
import com.jventrib.formulainfo.ui.common.composable.DataPoint
import com.jventrib.formulainfo.ui.common.composable.Serie
import com.jventrib.formulainfo.ui.common.composable.YOrientation
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun DriverStandingChart(
    season: Int,
    standings: Map<Driver, List<DriverStanding>>,
    onStandingClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$season standing",
                        modifier = Modifier.clickable {}
                    )
                },
                actions = {
                    IconButton(
                        onClick = onStandingClicked,
                        modifier = Modifier.semantics { testTag = "standing" }
                    ) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                    }
                }
            )
        }
    ) {

// ) {
        val series = standings.map { entry ->
            Serie(
                entry.value.map { round ->
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

// @Preview(showSystemUi = false)
// @Composable
// fun SeasonChartPreview() {
//    SeasonChart(race = null, lapsByResult = lapsWithStart)
// }
