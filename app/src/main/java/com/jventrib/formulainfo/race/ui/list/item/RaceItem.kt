package com.jventrib.formulainfo.race.ui.list.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.jventrib.formulainfo.common.ui.customDateTimeFormatter
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.ui.theme.LightLightGrey
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun RaceItem(raceFull: RaceFull) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = LightLightGrey
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(raceFull.circuit.location.flag),
                null,
                Modifier
                    .padding(start = 8.dp)
                    .size(64.dp)
            )
            Column(Modifier.padding(8.dp)) {
                Text(text = raceFull.race.raceName, style = MaterialTheme.typography.h6)
                Text(
                    text = ZonedDateTime.ofInstant(
                        raceFull.race.sessions.race,
                        ZoneId.systemDefault()
                    )
                        .format(customDateTimeFormatter), style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    RaceItem(raceFull = getRaceFullSample(1, "Emilia Romagna Grand Prix"))
}