package com.jventrib.formulainfo.race.ui.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.list.item.RaceItem

@Composable
fun RaceList(raceList: StoreResponse<List<RaceFull>>) {
    LazyColumn {
        raceList.dataOrNull()?.let { raceList ->
            items(raceList) {
                RaceItem(it)
            }
        }
    }
}