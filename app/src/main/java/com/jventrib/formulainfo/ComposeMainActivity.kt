package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.time.Instant

@ExperimentalStoreApi
@ExperimentalCoroutinesApi
@FlowPreview
class ComposeMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val raceRepository = (application as Application).appContainer.raceRepository
        val viewModel = MainViewModel(raceRepository)
        super.onCreate(savedInstanceState)
        viewModel.setSeasonPosition(0)
        setContent {
            FormulaInfoTheme {
                val raceList by viewModel.races.observeAsState(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
                RaceList(raceList)
            }
        }
    }
}

@Composable
fun RaceList(raceList: StoreResponse<List<RaceFull>>) {
    Column {
        raceList.dataOrNull()?.forEach {
            Text(text = it.race.raceName)
        }
    }
}


@Preview
@Composable
fun RaceListPreview() {
    val sessions = Race.Sessions(race = Instant.now())
    val race = Race(2021, 1, "", "Race1", "Circuit1", sessions)
    val race2 = Race(2021, 2, "", "Race2", "Circuit1", sessions)
    val circuit = Circuit("Circuit1", "url", "Circuit one", Circuit.Location(1.0f, 1.0f, "Fr", "France", "flag"), "url")
    RaceList(raceList = StoreResponse.Data(listOf(RaceFull(race, circuit), RaceFull(race2, circuit)), ResponseOrigin.SourceOfTruth))
}