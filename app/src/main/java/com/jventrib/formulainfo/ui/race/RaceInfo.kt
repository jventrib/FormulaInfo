package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsScore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.common.composable.ItemCard
import com.jventrib.formulainfo.ui.common.formatDateRange
import com.jventrib.formulainfo.ui.common.formatDateTime
import com.jventrib.formulainfo.ui.common.formatTime
import com.jventrib.formulainfo.ui.common.raceCountDownFormat
import com.jventrib.formulainfo.ui.schedule.Podium
import com.jventrib.formulainfo.ui.schedule.getRaceSample
import com.jventrib.formulainfo.utils.countDownFlow
import com.jventrib.formulainfo.utils.now
import java.time.Instant
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

@Composable
fun RaceInfo(
    race: Race,
    results: List<Result> = listOf(),
    mode: RaceInfoMode = RaceInfoMode.Mini,
    onRaceSelected: (Race) -> Unit = {}
) {
    ItemCard(
        image = {
            if (mode >= RaceInfoMode.Expanded) {
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
                            .height(42.dp)
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
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .width(64.dp)
                        .height(42.dp)
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
                if (mode >= RaceInfoMode.Expanded) {
                    race.raceInfo.sessions.fp1?.let { SessionDateText(it, "FP1") }
                    race.raceInfo.sessions.fp2?.let { SessionDateText(it, "FP2") }
                    race.raceInfo.sessions.fp3?.let { SessionDateText(it, "FP3") }
                    race.raceInfo.sessions.qualifying?.let { SessionDateText(it, "Qual") }
                    race.raceInfo.sessions.sprint?.let { SessionDateText(it, "Sprint") }
                    SessionDateText(
                        race.raceInfo.sessions.race,
                        "Race",
                        MaterialTheme.typography.body1
                    )
                } else {
                    SessionDateRangeText(race.raceInfo.sessions.fp1, race.raceInfo.sessions.race)
                }
                if (mode == RaceInfoMode.Maxi) {
                    CountDown(to = race.raceInfo.sessions.race)
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

enum class RaceInfoMode {
    Mini, Expanded, Maxi
}

@Composable
private fun SessionDateText(
    it: Instant,
    label: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.body2
) {
    Text(
        text = listOfNotNull(label, it.formatDateTime()).joinToString(": "),
        style = textStyle,
        fontWeight = if (it.isAfter(now())) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
private fun SessionDateRangeText(
    from: Instant?,
    to: Instant,
    textStyle: TextStyle = MaterialTheme.typography.body2
) {
    Row(
        verticalAlignment = CenterVertically

    ) {
        Text(
            text = formatDateRange(from, to),
            style = textStyle,
            fontWeight = if (to.isAfter(now())) FontWeight.Bold else FontWeight.Normal
        )
        if (to.isAfter(now())) {
            Spacer(Modifier.width(10.0.dp))
            Icon(
                Icons.Outlined.SportsScore,
                contentDescription = "Race start time",
                modifier = Modifier.size(width = 14.dp, height = 14.dp)
            )
            Text(
                text = to.formatTime(),
                style = textStyle,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CountDown(to: Instant) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val countDown by remember(lifecycleOwner) {
        to.countDownFlow(1.seconds).flowWithLifecycle(lifecycleOwner.lifecycle)
    }.collectAsState(initial = java.time.Duration.between(now(), to).toKotlinDuration())
    countDown
        .toComponents { days, hours, minutes, seconds, _ ->
            Text(
                text = raceCountDownFormat(
                    LocalContext.current.resources,
                    days, hours, minutes, seconds
                ),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }
}

@Preview(showBackground = true)
@Composable
fun RaceItemPreview() {
    RaceInfo(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
        mode = RaceInfoMode.Mini
    ) {}
}

@Preview(showBackground = true)
@Composable
fun RaceItemExpandedPreview() {
    RaceInfo(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
        mode = RaceInfoMode.Expanded
    ) {}
}

@Preview(showBackground = true)
@Composable
fun RaceItemMaxiPreview() {
    RaceInfo(
        race = getRaceSample(1, "Emilia Romagna Grand Prix"),
        results = listOf(
            getResultSample("Verstappen", 1),
            getResultSample("Hamilton", 2),
            getResultSample("Bottas", 3),
        ),
        mode = RaceInfoMode.Maxi
    ) {}
}
