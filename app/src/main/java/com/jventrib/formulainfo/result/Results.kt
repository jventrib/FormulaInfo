package com.jventrib.formulainfo.result

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.jventrib.formulainfo.race.model.db.RaceResultFull
import com.jventrib.formulainfo.race.ui.components.ItemCard

@Composable
fun Results(results: List<RaceResultFull>) {
    LazyColumn {
        items(results) {
            ResultItem(it, onResultSelected = { })
        }
    }

}

@Composable
fun ResultItem(it: RaceResultFull, onResultSelected: () -> Any) {
    ItemCard(image = it.driver.image,
        onItemSelected = {

        })
    {
        Text(
            text = "${it.raceResult.position}:${it.driver.givenName} ${it.driver.familyName}",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = it.constructor.name,
            style = MaterialTheme.typography.body1
        )
        Text(
            text = it.raceResult.time?.time ?: "",
            style = MaterialTheme.typography.body2
        )
    }
}
