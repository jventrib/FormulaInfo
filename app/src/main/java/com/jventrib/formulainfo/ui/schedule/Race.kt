package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.common.composable.ItemCard
import com.jventrib.formulainfo.ui.common.format
import com.jventrib.formulainfo.ui.results.getResultSample
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
        image = {
            if (expanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp)
                ) {
                    Image(
                        imageModel = race.circuit.location.flag,
                        modifier = Modifier
                            .padding(top = 13.dp)
                            .width(64.dp)
                            .height(38.dp)
                            .clip(RectangleShape),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = race.circuit.location.locality,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Image(
                    imageModel = race.circuit.location.flag,
                    modifier = Modifier
                        .padding(vertical = 13.dp, horizontal = 8.dp)
                        .width(64.dp)
                        .height(38.dp)
                        .clip(RectangleShape),
                    contentScale = ContentScale.FillBounds
                )
            }
        },
        onItemSelected = { onRaceSelected(race) },
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = race.raceInfo.raceName,
                    style = MaterialTheme.typography.h6
                )
                if (expanded) {
                    race.raceInfo.sessions.fp1?.let { SessionDateText(it, "FP1") }
                    race.raceInfo.sessions.fp2?.let { SessionDateText(it, "FP2") }
                    race.raceInfo.sessions.fp3?.let { SessionDateText(it, "FP3") }
                    race.raceInfo.sessions.qualifying?.let { SessionDateText(it, "Qual") }
                    SessionDateText(race.raceInfo.sessions.race, "Race")
                } else {
                    SessionDateText(race.raceInfo.sessions.race)
                }
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
private fun SessionDateText(it: Instant, label: String? = null) {
    Text(
        text = listOfNotNull(label, it.format()).joinToString(": "),
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
