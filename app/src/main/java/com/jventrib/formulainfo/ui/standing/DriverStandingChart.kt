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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.jventrib.formulainfo.model.aggregate.DriverAndConstructor
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.ui.common.composable.Chart
import com.jventrib.formulainfo.ui.common.composable.DataPoint
import com.jventrib.formulainfo.ui.common.composable.Serie
import com.jventrib.formulainfo.ui.common.composable.YOrientation
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.drivers.driverSelectionSaver
import com.jventrib.formulainfo.ui.theme.teamColor
import logcat.logcat

@Composable
fun DriverStandingChart(
    season: Int,
    standings: Map<Driver, List<DriverStanding>>,
    onStandingClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    // val pairs = standings.keys.map { it.driverId to true }.toMutableStateMap()

    val selectedDrivers = rememberSaveable(standings, key = "standingDrivers", saver = driverSelectionSaver) {
        // val selectedDrivers = remember(pairs) {
        logcat("Standing") { "Init standings: ${standings.count()}" }
        standings.keys.map { it.driverId to true }.toMutableStateMap()
    }

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
        },
        drawerShape = customShape(),
        drawerContent = {
            DriverSelector(
                drivers = standings.values.map { it.last() }.sortedByDescending { it.points }
                    .map { DriverAndConstructor(it.driver, it.constructor) },
                selectedDrivers
            )
        }

    ) {

// ) {
        val series = standings
            .filter {
                selectedDrivers[it.key.driverId] ?: false
            }
            .map { entry ->
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
            series = series, yOrientation = YOrientation.Up, gridStep = Offset(5f, 10f),
        )
    }
}

// @Preview(showSystemUi = false)
// @Composable
// fun SeasonChartPreview() {
//    SeasonChart(race = null, lapsByResult = lapsWithStart)
// }
