package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp


@Composable
fun <E> Chart(
    series: List<Serie<E>>,
    modifier: Modifier = Modifier,
    valueBound: ValueBound? = null,
) {
    var scrollOffset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(2f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    fun scaleCoerced() = scale.coerceAtLeast(1f)


    fun <E> DrawScope.drawSerie(
        serie: Serie<E>,
        valueBound: ValueBound
    ) {
        val seriePoints = serie.seriePoints
        val screenCenterX = size.width / 2f
        fun scrollCoerced() = scrollOffset.coerceIn(
            -screenCenterX + screenCenterX / scaleCoerced(),
            screenCenterX - screenCenterX / scaleCoerced()
        )

        fun getElementXY(dataPoint: DataPoint<E>): Offset {
            val xFraction = valueBound.run { (dataPoint.x - minX!!) / (maxX!! - minX) }
            val yFraction = valueBound.run { (dataPoint.y - minY!!) / (maxY!! - minY) }
            val lerp = lerp(-screenCenterX, screenCenterX, xFraction)
            val x = (lerp + scrollOffset) * scaleCoerced() + screenCenterX
            val y = lerp(0f, size.height, yFraction)
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
                        scrollOffset = scrollOffset.plus(delta / scaleCoerced())
                        delta
                    }, Orientation.Horizontal
                )
                .transformable(transformState),
        ) {
            val minX = valueBound?.minX ?: series.minOfOrNull { it.seriePoints.minOf { it.x } }
            val maxX = valueBound?.maxX ?: series.maxOfOrNull { it.seriePoints.maxOf { it.x } }
            val minY = valueBound?.minY ?: series.minOfOrNull { it.seriePoints.minOf { it.y } }
            val maxY = valueBound?.maxY ?: series.maxOfOrNull { it.seriePoints.maxOf { it.y } }

            val vb = ValueBound(minX, maxX, minY, maxY)
            series.forEach { serie ->
                drawSerie(serie, vb)
            }
        }
    }
}

data class Serie<E>(val seriePoints: List<DataPoint<E>>, val color: Color)

data class DataPoint<E>(val x: Float, val y: Float, val element: E)

data class ValueBound(
    val minX: Float? = null,
    val maxX: Float? = null,
    val minY: Float? = null,
    val maxY: Float? = null
)

