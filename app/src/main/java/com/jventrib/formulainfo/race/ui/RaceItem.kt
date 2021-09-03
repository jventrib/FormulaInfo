package com.jventrib.formulainfo.race.ui.list.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.common.ui.format
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.components.ItemCard

@Composable
fun RaceItem(
    raceFull: RaceFull,
    placeholder: Int = R.drawable.loading,
    onRaceSelected: (RaceFull) -> Unit = {}
) {
    ItemCard(
        raceFull.circuit.location.flag,
        placeholder,
        raceFull.race.raceName,
        raceFull.race.sessions.race.format(),
        onItemSelected = {
            onRaceSelected(raceFull) }
    )
}

@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    RaceItem(
        raceFull = getRaceFullSample(1, "Emilia Romagna Grand Prix"),
        placeholder = R.drawable.japan,
        {}
    )
}