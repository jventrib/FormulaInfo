package com.jventrib.formulainfo.race.ui.list.item

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.components.ItemCard

@Composable
fun RaceItem(
    raceFull: RaceFull,
    onRaceSelected: (RaceFull) -> Unit = {}
) {
    ItemCard(raceFull.circuit.location.flag!!,
        {
        onRaceSelected(raceFull)
    })
    {
        Text(text = raceFull.race.raceName,
            style = MaterialTheme.typography.h6)
        Text(
            text = raceFull.race.sessions.race.format(),
            style = MaterialTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    RaceItem(
        raceFull = getRaceFullSample(1, "Emilia Romagna Grand Prix")
    ) {}
}