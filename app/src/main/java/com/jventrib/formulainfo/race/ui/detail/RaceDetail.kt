package com.jventrib.formulainfo.race.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.list.item.RaceItem

@Composable
fun RaceDetail(viewModel: MainViewModel, navController: NavHostController, raceFull: RaceFull) {

    val race by viewModel.race.observeAsState(StoreResponse.Loading(ResponseOrigin.SourceOfTruth))
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable { navController.navigate("about") })
                },
                actions = {
                }
            )
        }) {
        RaceItem(raceFull = raceFull)
    }


}