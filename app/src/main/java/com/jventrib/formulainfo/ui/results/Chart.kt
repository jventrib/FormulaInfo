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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp
import kotlin.random.Random

val intervals = floatArrayOf(20f, 20f)
val pathEffect = PathEffect.dashPathEffect(intervals)

@Composable
fun <E> Chart(
    series: List<Serie<E>>,
    modifier: Modifier = Modifier,
    boundary: Boundary? = null,
) {
    var scrollOffset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(2f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale = (scale * zoomChange).coerceAtLeast(1f)
        rotation += rotationChange
        offset += offsetChange
    }
    val scrollState = rememberScrollableState { delta ->
        scrollOffset = scrollOffset.plus(delta / scale)
        delta
    }

    fun <E> DrawScope.drawSerie(
        serie: Serie<E>,
        boundary: Boundary
    ) {
        val seriePoints = serie.seriePoints
        val screenCenterX = size.width / 2f
        scrollOffset = scrollOffset.coerceIn(
            -screenCenterX + screenCenterX / scale,
            screenCenterX - screenCenterX / scale
        )

        val screen = Rect(Offset.Zero - Offset(size.width * 1, 0f), size * 4f)
        val points = seriePoints.map {
            getElementXY(it, boundary, scrollOffset, scale)
        }.filter { screen.contains(it) }

        drawPoints(
            points,
            PointMode.Polygon,
            serie.color,
            3.dp.toPx(),
            StrokeCap.Round,
        )
        points.forEach { drawCircle(color = serie.color, 4.dp.toPx(), it) }
    }


    Box() {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .border(1.dp, Color.Black)
                .scrollable(scrollState, Orientation.Horizontal)
                .transformable(transformState),
        ) {
            val minX = boundary?.minX ?: series.minOfOrNull { it.seriePoints.minOf { it.x } }
            val maxX = boundary?.maxX ?: series.maxOfOrNull { it.seriePoints.maxOf { it.x } }
            val minY = boundary?.minY ?: series.minOfOrNull { it.seriePoints.minOf { it.y } }
            val maxY = boundary?.maxY ?: series.maxOfOrNull { it.seriePoints.maxOf { it.y } }

            val vb = Boundary(minX, maxX, minY, maxY)
            series.forEach { serie ->
                drawSerie(serie, vb)
            }
        }
    }
}


fun <E> DrawScope.getElementXY(
    dataPoint: DataPoint<E>,
    boundary: Boundary,
    scrollOffset: Float,
    scale: Float
): Offset {
    val xFraction = boundary.run { (dataPoint.x - minX!!) / (maxX!! - minX) }
    val yFraction = boundary.run { (dataPoint.y - minY!!) / (maxY!! - minY) }
    val screenCenterX = size.width / 2f
    val lerp = lerp(-screenCenterX, screenCenterX, xFraction)
    val x = (lerp + scrollOffset) * scale + screenCenterX
    val y = lerp(0f, size.height, yFraction)
    return Offset(x, y)
}

data class Serie<E>(val seriePoints: List<DataPoint<E>>, val color: Color)

data class DataPoint<E>(val x: Float, val y: Float, val element: E)

data class Boundary(
    val minX: Float? = null,
    val maxX: Float? = null,
    val minY: Float? = null,
    val maxY: Float? = null
)

@Preview(showSystemUi = false)
@Composable
fun ChartPreview() {
    val series = (0..5).map {
        Serie(
            (0..10).map {
                DataPoint(it.toFloat(), Random.nextInt(20).toFloat(), "TEST")
            }, Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
        )
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
                .fillMaxHeight(1f)
                .border(2.dp, Color.Red),
        )
    }
}