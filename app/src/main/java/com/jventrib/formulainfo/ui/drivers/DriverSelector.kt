package com.jventrib.formulainfo.ui.drivers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.aggregate.DriverAndConstructor
import com.jventrib.formulainfo.ui.theme.color

@Composable
fun DriverSelector(
    drivers: List<DriverAndConstructor>,
    selectState: MutableMap<String, Boolean>
) {
    rememberDrawerState(DrawerValue.Open)
    val scrollState = rememberScrollState()
    val allState = when {
        selectState.values.all { it } -> ToggleableState.On
        selectState.values.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    val onAllClick = {
        val s = allState != ToggleableState.On
        selectState.forEach {
            selectState[it.key] = s
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .verticalScroll(scrollState)
    ) {

        Row(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(vertical = 2.dp),
            verticalAlignment = CenterVertically
        ) {
            Box(modifier = Modifier.weight(.5f)) {
                Text(
                    text = "All",
                    color = Color.White,
                    modifier = Modifier
                        .align(Center)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Black)
                        .padding(4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(.5f)
                    .height(8.dp)
                // .wrapContentSize(Center, true)
            ) {
                TriStateCheckbox(
                    state = allState,
                    onClick = onAllClick,
                )
            }
        }

        drivers.forEach { result ->
            Row(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(vertical = 2.dp),
                verticalAlignment = CenterVertically
            ) {
                Box(modifier = Modifier.weight(.5f)) {
                    Text(
                        text = result.driver.code ?: result.driver.driverId.take(3),
                        color = Color.White,
                        modifier = Modifier
                            .align(Center)
                            .clip(MaterialTheme.shapes.medium)
                            .background(result.constructor.color)
                            .padding(4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(.5f)
                        .height(8.dp)
                    // .wrapContentSize(Center, true)
                ) {
                    Checkbox(
                        checked = selectState[result.driver.driverId] ?: false,
                        onCheckedChange = {
                            selectState[result.driver.driverId] = it
                        },
                    )
                }
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
        drawerContent = {
            DriverSelector(
                drivers = ResultSample.get202101Results()
                    .map { DriverAndConstructor(it.driver, it.constructor) },
                remember { mutableStateMapOf() }
            )
        },
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

val driverSelectionSaver = listSaver<SnapshotStateMap<String, Boolean>, Pair<String, Boolean>>(
    save = { it.toList() },
    restore = { it.toMutableStateMap() }
)
