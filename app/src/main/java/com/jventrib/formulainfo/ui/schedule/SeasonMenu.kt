package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeasonMenu(
    seasonList: List<Int>,
    selectedSeason: Int?,
    initiallyExpanded: Boolean = false,
    onSeasonSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = "$selectedSeason",
            onValueChange = {},
            label = { Text("Season") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            modifier = Modifier.width(115.dp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedLabelColor = LocalContentColor.current,
                unfocusedLabelColor = LocalContentColor.current,
                trailingIconColor = LocalContentColor.current,
                focusedTrailingIconColor = LocalContentColor.current,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            seasonList.forEach { season ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSeasonSelect(season)
                    }
                ) {
                    Text("$season")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SeasonMenuPreview() {
    FormulaInfoTheme(true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    actions = {
                        SeasonMenu(
                            seasonList = (2022..2030).toList(),
                            selectedSeason = 2022,
                            onSeasonSelect = {},
                            initiallyExpanded = true
                        )
                    }
                )
            }
        ) {}
    }
}
