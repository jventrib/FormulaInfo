package com.jventrib.formulainfo.ui.races.item

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.ui.components.ItemCard

@Composable
fun RaceItem(
    fullRace: FullRace,
    onRaceSelected: (FullRace) -> Unit = {}
) {
    ItemCard(
        fullRace.circuit.location.flag,
        {
        onRaceSelected(fullRace)
    },
        {
            Text(text = fullRace.race.raceName,
                style = MaterialTheme.typography.h6)
            Text(
                text = fullRace.race.sessions.race.format(),
                style = MaterialTheme.typography.body2
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    RaceItem(
        fullRace = getRaceFullSample(1, "Emilia Romagna Grand Prix")
    ) {}
}