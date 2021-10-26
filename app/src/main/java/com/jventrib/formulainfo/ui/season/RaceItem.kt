package com.jventrib.formulainfo.ui.season.item

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
                fullRace.race.sessions.fp1?.let {
                    Text(
                        text = it.format(),
                        style = MaterialTheme.typography.body2
                    )
                }
                fullRace.race.sessions.fp2?.let {
                    Text(
                        text = it.format(),
                        style = MaterialTheme.typography.body2
                    )
                }
                fullRace.race.sessions.fp3?.let {
                    Text(
                        text = it.format(),
                        style = MaterialTheme.typography.body2
                    )
                }
                fullRace.race.sessions.qualifying?.let {
                    Text(
                        text = it.format(),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
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
        fullRace = getRaceFullSample(1, "Emilia Romagna Grand Prix"),
        expanded = false
    ) {}
}
@Preview(showBackground = true)
@Composable
fun RaceItemPreviewExpanded() {
    RaceItem(
        fullRace = getRaceFullSample(1, "Emilia Romagna Grand Prix"),
        expanded = true
    ) {}
}