package com.jventrib.formulainfo.ui.schedule.item

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceSample
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.components.ItemCard
import java.time.Instant

@ExperimentalCoilApi
@Composable
fun Race(
    race: Race,
    expanded: Boolean = false,
    onRaceSelected: (Race) -> Unit = {}
) {
    ItemCard(
        image = race.circuit.location.flag,
        onItemSelected = { onRaceSelected(race) },
        content = {
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
    )
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
        expanded = false
    ) {}
}

@ExperimentalCoilApi
@Preview(showBackground = true)
@Composable
fun RaceItemPreviewExpanded() {
    Race(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        expanded = true
    ) {}
}