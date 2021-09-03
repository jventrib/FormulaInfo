package com.jventrib.formulainfo.race.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import com.jventrib.formulainfo.race.ui.components.Image
import com.jventrib.formulainfo.race.ui.list.item.RaceItem
import com.jventrib.formulainfo.result.Results

@Composable
fun RaceDetail(raceFull: RaceFull, raceResults: List<RaceResultFull>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable {})
                },
                actions = {
                }
            )
        }) {
        Column {
            RaceItem(raceFull = raceFull)
            Image(imageModel = raceFull.circuit.imageUrl)
            Results(results = raceResults)
        }
    }
}

@Preview
@Composable
fun RaceDetailPreview() {
    RaceDetail(raceFull = getRaceFullSample(3), listOf())
}