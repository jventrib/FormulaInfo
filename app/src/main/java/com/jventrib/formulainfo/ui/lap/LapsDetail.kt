package com.jventrib.formulainfo.ui.lap

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.db.FullResult
import com.jventrib.formulainfo.model.db.LapTime
import com.jventrib.formulainfo.result.DriverResult
import com.jventrib.formulainfo.ui.components.ItemCard
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@ExperimentalCoilApi
@Composable
fun LapsDetail(result: FullResult, laps: List<LapTime>) {
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
            DriverResult(result = result, onResultSelected = {})
            LazyColumn {
                items(laps) {
                    ItemCard(
                        topText = it.number.toString(),
                        bottomText = it.time.toLapTimeString(),
                        onItemSelected = {},
                        image = null
                    )
                }
            }
        }
    }
}

private fun Duration.toLapTimeString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss.SSS")
    return LocalTime.ofNanoOfDay(this.toNanos()).format(formatter)
}
