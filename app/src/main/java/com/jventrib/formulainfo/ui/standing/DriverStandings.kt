package com.jventrib.formulainfo.ui.standing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.getRaceSample
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Race

@ExperimentalCoilApi
@Composable
fun DriverStandingScreen(
    season: Int,
    race: Race?,
    standings: List<DriverStanding>,
    onDriverSelected: (driver: Driver) -> Unit,
    onRaceImageSelected: (Race) -> Unit,
    onChartClicked: (Race) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${race?.raceInfo?.raceName?.let { "$it " } ?: ""}$season standing",
                        modifier = Modifier.clickable {})
                },
//                actions = {
//                    IconButton(onClick = { onChartClicked(race) }) {
//                        Icon(imageVector = Icons.Filled.MultilineChart, contentDescription = null)
//                    }
//                }
            )
        }) {
        LazyColumn {
            items(items = standings) { result ->
                DriverStanding(result, onDriverSelected = { onDriverSelected(it.driver) })
            }
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun DriverStandingScreenPreview() {
    DriverStandingScreen(
        season = 2021,
        race = getRaceSample(3),
        standings = listOf(),
        onDriverSelected = {},
        onRaceImageSelected = {},
        onChartClicked = {}
    )
}
