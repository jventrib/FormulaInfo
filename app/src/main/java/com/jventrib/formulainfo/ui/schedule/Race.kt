package com.jventrib.formulainfo.ui.schedule.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceSample
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.components.ItemCard
import com.jventrib.formulainfo.ui.results.getResultSample
import com.jventrib.formulainfo.ui.schedule.Podium
import java.time.Instant

@ExperimentalCoilApi
@Composable
fun Race(
    race: Race,
    results: List<Result> = listOf(),
    expanded: Boolean = false,
    onRaceSelected: (Race) -> Unit = {}
) {
    ItemCard(
        image = race.circuit.location.flag,
        onItemSelected = { onRaceSelected(race) }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = race.raceInfo.raceName,
                    style = MaterialTheme.typography.h6
                )
                if (expanded) {
                    race.raceInfo.sessions.fp1?.let { SessionDateText(it) }
                    race.raceInfo.sessions.fp2?.let { SessionDateText(it) }
                    race.raceInfo.sessions.fp3?.let { SessionDateText(it) }
                    race.raceInfo.sessions.qualifying?.let { SessionDateText(it) }
                }
                SessionDateText(race.raceInfo.sessions.race)
            }
            if (results.size >= 3) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .align(CenterVertically)
                ) {
                    Podium(results, Modifier)
                }
            }
        }
    }
}

@Composable
private fun SessionDateText(it: Instant) {
    Text(
        text = it.format(),
        style = MaterialTheme.typography.body2,
        fontWeight = if (it.isAfter(Instant.now())) FontWeight.Bold else FontWeight.Normal
    )
}

@ExperimentalCoilApi
@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    Race(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
        expanded = false
    ) {}
}

@ExperimentalCoilApi
@Preview(showBackground = true)
@Composable
fun RaceItemPreviewExpanded() {
    Race(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
        expanded = true
    ) {}
}