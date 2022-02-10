package com.jventrib.formulainfo.ui.drivers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.theme.teamColor

@Composable
fun DriverSelector(drivers: List<Result>, selectState: MutableMap<String, Boolean>) {
    rememberDrawerState(DrawerValue.Open)

    drivers.forEach { result ->
        Row(modifier = Modifier.width(90.dp)) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(teamColor[result.constructor.id]!!)
                    .width(16.dp)
                    .align(CenterVertically)
            ) { Text("") }
            Box(modifier = Modifier.align(CenterVertically)) {
                Text(text = result.driver.code!!)
            }
            Column(horizontalAlignment = End, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = selectState[result.driver.driverId] ?: false, onCheckedChange = {
                    selectState[result.driver.driverId] = it
                })
            }
        }
    }
}

@Preview
@Composable
fun DriverSelectorPreview() {
    Scaffold(
        scaffoldState = rememberScaffoldState(
            drawerState =
            rememberDrawerState(initialValue = DrawerValue.Open)
        ),
        drawerShape = customShape(),
        drawerContent = { DriverSelector(drivers = ResultSample.get202101Results(), mutableMapOf()) },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Text("Test")
        }
    }
}

fun customShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                0f,
                0f,
                with(density) { 100.dp.toPx() } /* width */,
                size.height /* height */
            )
        )
    }
}
