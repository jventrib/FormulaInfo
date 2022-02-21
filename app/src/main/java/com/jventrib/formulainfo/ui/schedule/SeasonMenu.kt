package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SeasonMenu(
    seasonList: List<Int>,
    selectedSeason: Int?,
    initiallyExpanded: Boolean = false,
    onSeasonSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    Box(
        Modifier
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(selectedSeason.toString())
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        seasonList.forEach { season ->
            DropdownMenuItem(onClick = {
                expanded = false
                onSeasonSelect(season)
            }) {
                Text(
                    season.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
