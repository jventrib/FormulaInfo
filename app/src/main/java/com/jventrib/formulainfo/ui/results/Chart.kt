package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import logcat.logcat
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun <E> Chart(
    series: List<Serie<E>>,
    modifier: Modifier = Modifier,
    boundary: Boundary? = null
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
    val yOrigin = remember { mutableStateMapOf<Serie<E>, Offset?>() }

    //Coerce scrollOffset at each recomposition, so zoom out always keep inside boundaries
    scrollOffset = scrollOffset.coerceIn(
        -screenCenterX + screenCenterX / scale,
        screenCenterX - screenCenterX / scale
    )

    Row {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .background(Color(.9f, .9f, .9f, .9f))
                .padding(vertical = 16.dp)
                .zIndex(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            series.map { serie ->
                yOrigin[serie]?.let {
                    Text(
                        text = serie.label,
                        modifier = Modifier.offset(offset = {
                            IntOffset(0, it.y.roundToInt() - 12.dp.roundToPx())
                        })
                    )
                }
            }
        }
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 4.dp)
                .scrollable(scrollState, Orientation.Horizontal)
                .transformable(transformState)
                .onGloballyPositioned { size = it.size.toSize() },
        ) {
            val minX =
                boundary?.minX ?: series.minOfOrNull { serie ->
                    serie.seriePoints.minOfOrNull { it.x } ?: 0f
                } ?: 0f
            val maxX =
                boundary?.maxX ?: series.maxOfOrNull { serie ->
                    serie.seriePoints.maxOfOrNull { it.x } ?: 0f
                } ?: 0f
            val minY =
                boundary?.minY ?: series.minOfOrNull { serie ->
                    serie.seriePoints.minOfOrNull { it.y } ?: 0f
                } ?: 0f
            val maxY =
                boundary?.maxY ?: series.maxOfOrNull { serie ->
                    serie.seriePoints.maxOfOrNull { it.y } ?: 0f
                } ?: 0f

            val vb = Boundary(minX, maxX, minY, maxY)
            series.forEach { serie ->
                drawSerie(serie, vb, size, scrollOffset, scale, yOrigin)
            }
        }
    }
}

fun <E> DrawScope.drawSerie(
    serie: Serie<E>,
    boundary: Boundary,
    size: Size,
    scrollOffset: Float,
    scale: Float,
    yOrigin: SnapshotStateMap<Serie<E>, Offset?>
) {
    val seriePoints = serie.seriePoints
    val alpha = (20 * (scale - 1) / seriePoints.size).coerceIn(0f, 1f)

//    val screen = Rect(Offset.Zero - Offset(size.width * 1f, 0f), size * 4f)
    val points = seriePoints
        .map { getElementXY(it, boundary, scrollOffset, scale) }
//        .filter { screen.contains(it) }

    val start = points.lastOrNull { it.x <= 0.1f }
    val stop = points.firstOrNull { it.x > 0.1f }
    if (start != null && stop != null) stop.let {
        val fraction = start.x.absoluteValue / (stop.x - start.x)
        val yAxis = lerp(start, it, fraction)
        yOrigin[serie] = yAxis
    } else {
        logcat { "No stop, start: $start" }
        yOrigin[serie] = points.firstOrNull { it.x in -0.1f..0.1f }
    }
    logcat { "yAxisPoint: ${yOrigin[serie]}, points:" + points.joinToString(",") { it.x.toString() } }

    drawPoints(
        points,
        PointMode.Polygon,
        serie.color,
        3.dp.toPx(),
        StrokeCap.Round,
    )
    points.forEach { drawCircle(color = serie.color, 4.dp.toPx(), it, alpha) }
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

data class Serie<E>(val seriePoints: List<DataPoint<E>>, val color: Color, val label: String)

data class DataPoint<E>(val x: Float, val y: Float, val element: E?)

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