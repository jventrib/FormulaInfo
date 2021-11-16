package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp
import com.jventrib.formulainfo.data.sample.ResultSample
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.theme.teamColor
import logcat.logcat
import java.time.Duration


@Composable
fun <E> Chart(
    series: List<Serie<E>>,
    modifier: Modifier = Modifier,
    valueBound: ValueBound? = null,
) {
    var scrollOffset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    fun <E> DrawScope.drawSerie(
        serie: Serie<E>,
        valueBound: ValueBound
    ) {
        val seriePoints = serie.seriePoints
        val screenCenterX = size.width / 2f

        fun getElementXY(dataPoint: DataPoint<E>): Offset {
            val xFraction = valueBound.run { (dataPoint.x - minX!!) / (maxX!! - minX) }
            val yFraction = valueBound.run { (dataPoint.y - minY!!) / (maxY!! - minY) }
            val delta = screenCenterX
            val lerp = lerp(-delta, delta, xFraction)
            val x = (lerp + scrollOffset) * scale
//            * scale + delta
            val y =
                lerp(0f, size.height, yFraction)
            return Offset(x, y)
        }
        seriePoints.indices.forEach { index ->
            val currentElement = seriePoints[index]
            val nextElement = seriePoints.getOrNull(index + 1)

            val currentOffset = getElementXY(currentElement)
            nextElement?.let {
                val nextOffset = getElementXY(nextElement)
                drawLine(
                    color = serie.color,
                    start = currentOffset,
                    end = nextOffset,
                    strokeWidth = 3.dp.toPx()
                )
                drawCircle(color = serie.color, 10f, nextOffset)
            }
            drawCircle(color = serie.color, 10f, currentOffset)
        }
    }


    Box() {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .border(1.dp, Color.Black)
                .scrollable(
                    state = rememberScrollableState { delta ->
                        scrollOffset += delta
                        delta
                    }, Orientation.Horizontal
                )
                .transformable(transformState),
        ) {
            val minX = valueBound?.minX ?: series.minOf { it.seriePoints.minOf { it.x } }
            val maxX = valueBound?.maxX ?: series.maxOf { it.seriePoints.maxOf { it.x } }
            val minY = valueBound?.minY ?: series.minOf { it.seriePoints.minOf { it.y } }
            val maxY = valueBound?.maxY ?: series.maxOf { it.seriePoints.maxOf { it.y } }

            val vb = ValueBound(minX, maxX, minY, maxY)
            series.forEach { serie ->
                drawSerie(serie, vb)
            }
        }
    }
}

data class Serie<E>(val seriePoints: List<DataPoint<E>>, val color: Color) {

}


data class DataPoint<E>(val x: Float, val y: Float, val element: E)

data class ValueBound(
    val minX: Float? = null,
    val maxX: Float? = null,
    val minY: Float? = null,
    val maxY: Float? = null
)

private fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>): Map<Result, List<Lap>> =
    lapsByResult
        .mapValues { entry ->
            entry.value
                .toMutableList().apply {
                    if (entry.key.resultInfo.grid != 0) {
                        add(
                            0, Lap(
                                entry.key.resultInfo.season,
                                entry.key.resultInfo.round,
                                entry.key.driver.driverId,
                                entry.key.driver.code ?: entry.key.driver.driverId,
                                0,
                                entry.key.resultInfo.grid,
                                Duration.ZERO,
                                Duration.ZERO
                            )
                        )
                    }
                }
        }

@Preview(showSystemUi = false)
@Composable
fun ChartPreview() {
    val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
    val series = lapsWithStart.map { entry ->
        Serie(entry.value.map { lap ->
            DataPoint(
                lap.number.toFloat(),
                lap.position.toFloat(),
                lap
            )
        }, teamColor[entry.key.constructor.id]!!)
    }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(3.dp, Color.Blue)
    ) {
        Chart(
            series = series,
            modifier = Modifier
                .fillMaxHeight(.5f)
                .border(2.dp, Color.Red),
            valueBound = ValueBound(maxY = 20f)
        )
    }
//    Chart(map, maxYValue = lapsWithStart.size.toFloat())
}