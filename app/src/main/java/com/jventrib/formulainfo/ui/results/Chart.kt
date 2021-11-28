package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.google.android.material.math.MathUtils.lerp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun <E> Chart(
    series: List<Serie<E>>,
    modifier: Modifier = Modifier,
    boundaries: Boundaries? = null
) {
    var scrollOffset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale = (scale * zoomChange).coerceAtLeast(1f)
        rotation += rotationChange
        offset += offsetChange
    }
    var size by remember { mutableStateOf(Size.Zero) }
    val screenCenterX = size.width / 2f
    val scrollState = rememberScrollableState { delta ->
        scrollOffset = scrollOffset.plus(delta / scale)
        delta
    }

    //Coerce scrollOffset at each recomposition, so zoom out always keep inside boundaries
    scrollOffset = scrollOffset.coerceIn(
        -screenCenterX + screenCenterX / scale,
        screenCenterX - screenCenterX / scale
    )
    val adaptedBoundaries = getBoundaries(boundaries, series)

    val maxOf = series.maxOfOrNull { it.seriePoints.size } ?: 1
    val pointAlpha = (20 * (scale - 1) / maxOf).coerceIn(0f, 1f)

    val windowSeries = series.map { serie ->
        getSeriePoints(serie, size, scale, adaptedBoundaries, scrollOffset)
    }

    Row {
        YAxis(windowSeries)
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 4.dp)
                .scrollable(scrollState, Orientation.Horizontal)
                .transformable(transformState)
                .onGloballyPositioned { size = it.size.toSize() },
        ) {
            windowSeries.forEach { serieScreen ->
                drawSerie(serieScreen.points, serieScreen.color, pointAlpha)
            }
        }
    }
}

@Composable
private fun YAxis(seriesPoints: List<WindowSerie>) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .background(Color(.9f, .9f, .9f, .9f))
            .padding(vertical = 16.dp)
            .zIndex(1f),
        contentAlignment = Alignment.TopCenter
    ) {
        seriesPoints.map { serie ->
            val yOrigin = serie.yOrigin
            if (yOrigin != null && !yOrigin.y.isNaN()) {
                Text(
                    text = serie.label,
                    modifier = Modifier.offset(offset = {
                        IntOffset(0, yOrigin.y.roundToInt() - 12.dp.roundToPx())
                    })
                )
            } else null
        }
    }
}

private fun <E> getBoundaries(
    boundaries: Boundaries?,
    series: List<Serie<E>>
): Boundaries {
    val minX =
        boundaries?.minX ?: series.filterNot { it.seriePoints.isEmpty() }.minOfOrNull { serie ->
            serie.seriePoints.minOfOrNull { it.x } ?: 0f
        } ?: 0f
    val maxX =
        boundaries?.maxX ?: series.filterNot { it.seriePoints.isEmpty() }.maxOfOrNull { serie ->
            serie.seriePoints.maxOfOrNull { it.x } ?: 0f
        } ?: 0f
    val minY =
        boundaries?.minY ?: series.filterNot { it.seriePoints.isEmpty() }.minOfOrNull { serie ->
            serie.seriePoints.minOfOrNull { it.y } ?: 0f
        } ?: 0f
    val maxY =
        boundaries?.maxY ?: series.filterNot { it.seriePoints.isEmpty() }.maxOfOrNull { serie ->
            serie.seriePoints.maxOfOrNull { it.y } ?: 0f
        } ?: 0f

    val vb = Boundaries(minX, maxX, minY, maxY)
    return vb
}

fun DrawScope.drawSerie(
    points: List<Offset>,
    color: Color,
    alpha: Float,
) {
    drawPoints(
        points,
        PointMode.Polygon,
        color,
        3.dp.toPx(),
        StrokeCap.Round,
    )
    points.forEach { drawCircle(color = color, 4.dp.toPx(), it, alpha) }
}

private fun <E> getSeriePoints(
    serie: Serie<E>,
    size: Size,
    scale: Float,
    boundaries: Boundaries,
    scrollOffset: Float,
): WindowSerie {
    val points = serie.seriePoints
        .map { getElementXY(it, size, boundaries, scrollOffset, scale) }

    val start = points.lastOrNull { it.x <= 0.1f }
    val stop = points.firstOrNull { it.x > 0.1f }
    val yOrigin = if (start != null && stop != null) {
        val fraction = start.x.absoluteValue / (stop.x - start.x)
        lerp(start, stop, fraction)
    } else {
        points.firstOrNull { it.x in -0.1f..0.1f }
    }
    return WindowSerie(points, yOrigin, serie.color, serie.label)
}


private fun <E> getElementXY(
    dataPoint: DataPoint<E>,
    size: Size,
    boundaries: Boundaries,
    scrollOffset: Float,
    scale: Float
): Offset {
    val xFraction = boundaries.run { (dataPoint.x - minX!!) / (maxX!! - minX) }
    val yFraction = boundaries.run { (dataPoint.y - minY!!) / (maxY!! - minY) }
    val screenCenterX = size.width / 2f
    val lerp = lerp(-screenCenterX, screenCenterX, xFraction)
    val x = (lerp + scrollOffset) * scale + screenCenterX
    val y = lerp(0f, size.height, yFraction)
    return Offset(x, y)
}

data class Serie<E>(val seriePoints: List<DataPoint<E>>, val color: Color, val label: String)
data class WindowSerie(
    val points: List<Offset>,
    val yOrigin: Offset?,
    val color: Color,
    val label: String
)

data class DataPoint<E>(val x: Float, val y: Float, val element: E?)

data class Boundaries(
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
            }, Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
            "TEST"
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