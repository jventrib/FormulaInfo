package com.jventrib.formulainfo.ui.schedule.item

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.ui.components.ItemCard
import java.time.Instant

@ExperimentalCoilApi
@Composable
fun Race(
    fullRace: FullRace,
    expanded: Boolean = false,
    onRaceSelected: (FullRace) -> Unit = {}
) {
    ItemCard(
        image = fullRace.circuit.location.flag,
        onItemSelected = { onRaceSelected(fullRace) },
        content = {
            Text(
                text = fullRace.race.raceName,
                style = MaterialTheme.typography.h6
            )
            if (expanded) {
                fullRace.race.sessions.fp1?.let { SessionDateText(it) }
                fullRace.race.sessions.fp2?.let { SessionDateText(it) }
                fullRace.race.sessions.fp3?.let { SessionDateText(it) }
                fullRace.race.sessions.qualifying?.let { SessionDateText(it) }
            }
            SessionDateText(fullRace.race.sessions.race)
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
        fullRace = getRaceFullSample(1, "Emilia Romagna Grand Prix"),
        expanded = false
    ) {}
}

@ExperimentalCoilApi
@Preview(showBackground = true)
@Composable
fun RaceItemPreviewExpanded() {
    Race(
        fullRace = getRaceFullSample(1, "Emilia Romagna Grand Prix"),
        expanded = true
    ) {}
}