package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LapChartMenu(
    selectedChart: String?,
    onChartSelect: (Charts) -> Unit

) {
    var expanded by remember { mutableStateOf(false) }
    val onChartSelectAndClose: (Charts) -> Unit = {
        expanded = false
        onChartSelect(it)
    }
    Box(
        Modifier
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier

        ) {
            Text(selectedChart.toString())
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ChartItem(onChartSelectAndClose, Charts.Position)
        ChartItem(onChartSelectAndClose, Charts.Time)
    }

}

@Composable
private fun ChartItem(
    onChartSelect: (Charts) -> Unit,
    chart: Charts
) {
    DropdownMenuItem(onClick = {
        onChartSelect(chart)
    }, modifier = Modifier.sizeIn(maxWidth = 170.dp)) {
        Text(chart.label)
    }
}

@Preview
@Composable
fun LapChartMenuPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formula Info") },
                actions = {
                    LapChartMenu("Chart1") {}
                }
            )
        },
    ) {
    }
}