package com.jventrib.formulainfo.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
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
    boundaries: Boundaries? = null,
    yOrientation: YOrientation,
    gridStep: Offset? = null,
    customDraw: DrawScope.(List<Serie<E>>) -> Unit = {}
) {
    val axisColor = colors.onBackground.toArgb()
    var scrollOffset by remember { mutableStateOf(Offset(1f, 1f)) }
    var scale by remember { mutableStateOf(Offset(1f, 1f)) }
    var rotation by remember { mutableStateOf(0f) }
    val onGesture: (centroid: Offset, pan: Offset, zoom: Offset, rotation: Float) -> Unit =
        { _, offsetChange, zoomChange, rotationChange ->
            scale =
                abs(scale * zoomChange.coerceAtMost(Offset(2f, 2f))).coerceIn(
                    Offset(1f, 1f), Offset(50f, 25f)
                )
            rotation += rotationChange
            scrollOffset += offsetChange / scale
        }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
    ) {
        val constraintSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        var size by remember { mutableStateOf(constraintSize) }
        val screenCenterX = size.width / 2f
        val screenCenterY = size.height / 2f
        val onGloballyPositioned: (LayoutCoordinates) -> Unit = { size = it.size.toSize() }

        //Coerce scrollOffset at each recomposition, so zoom out always keep inside boundaries
        scrollOffset = scrollOffset.let {
            Offset(
                it.x.coerceIn(
                    -screenCenterX + screenCenterX / scale.x,
                    screenCenterX - screenCenterX / scale.x
                ), it.y.coerceIn(
                    -screenCenterY + screenCenterY / scale.y,
                    screenCenterY - screenCenterY / scale.y
                )
            )
        }
        val adaptedBoundaries = getBoundaries(boundaries, series)

        val allSeriesSize = series.maxOfOrNull { it.seriePoints.size } ?: 1
        val pointAlpha = (20 * (scale.getDistance() - 1) / allSeriesSize).coerceIn(0f, 1f)


        val onScreenSeries = series.map { serie ->
            getSeriePoints(serie, size, scale, adaptedBoundaries, scrollOffset, yOrientation)
        }

        val state = ChartState(
            series,
            size,
            adaptedBoundaries,
            scrollOffset,
            scale,
            yOrientation,
            gridStep
        )

        Row {
            YAxis(onScreenSeries)
            Canvas(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 4.dp)
                    .pointerInput(Unit) { detectTransformGesturesXY(onGesture = onGesture) }
                    .onGloballyPositioned(onGloballyPositioned)
            ) {
                //Draw Grid
                drawGrid(state, axisColor)

                //Series
                onScreenSeries.forEach { serieScreen ->
                    drawSerie(serieScreen.seriePoints, serieScreen.color, pointAlpha)
                }

                //custom
                customDraw(onScreenSeries)
            }
        }
    }
}

data class ChartState<E>(
    val series: List<Serie<E>>,
    val size: Size,
    val adaptedBoundaries: Boundaries,
    val scrollOffset: Offset,
    val scale: Offset,
    val yOrientation: YOrientation,
    val gridStep: Offset?
)

private fun <E> DrawScope.drawGrid(state: ChartState<E>, axisColor: Int) {
    //Compute grid coord
    if (state.series.isNotEmpty() && state.gridStep != null) {
        val onScreenGrid = getOnScreenGrid(state)
        val verticalPadding = 16.dp.toPx() * 2
        val horizontalPadding = 4.dp.toPx()
        val axisLabelPaint = Paint().asFrameworkPaint().apply {
            textSize = 32f
            color = axisColor
        }
        val offsetX = onScreenGrid.offset.x  % onScreenGrid.step.x + onScreenGrid.step.x
        generateSequence(-onScreenGrid.step.x) { it + onScreenGrid.step.x }.takeWhile { it <= this.size.width + onScreenGrid.step.x * 4 }
            .forEachIndexed { index, x ->
                drawLine(
                    Color.LightGray,
                    start = Offset(x + offsetX, -verticalPadding),
                    end = Offset(x + offsetX, this.size.height + verticalPadding)
                )
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        (onScreenGrid.offset.x / +index).toString(),
                        x + offsetX,
                        this.size.height + 12.dp.toPx(),
                        axisLabelPaint
                    )
                }
            }
        generateSequence(-onScreenGrid.step.y * 4) { it + onScreenGrid.step.y }.takeWhile { it <= this.size.height + onScreenGrid.step.y * 4 }
            .forEach {
                drawLine(
                    Color.LightGray,
                    start = Offset(-horizontalPadding, it + onScreenGrid.offset.y),
                    end = Offset(
                        this.size.width + horizontalPadding,
                        it + onScreenGrid.offset.y
                    )
                )
            }
    }
}

private fun <E> getOnScreenGrid(state: ChartState<E>): Grid {
    val grid = state.run {
        val offset0 =
            getOnScreenPoint(
                DataPoint("GridX", Offset(0f, 0f)),
                size,
                adaptedBoundaries,
                scrollOffset,
                scale,
                yOrientation
            )

        val offset1 =
            getOnScreenPoint(
                DataPoint("GridX", gridStep!!),
                size,
                adaptedBoundaries,
                scrollOffset,
                scale,
                yOrientation
            )

        val step = offset1 - offset0
        Grid(step, offset0)
    }
    return grid
}

data class Grid(val step: Offset, val offset: Offset)


@Composable
private fun <E> YAxis(seriesPoints: List<Serie<E>>) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .background(colors.surface.copy(alpha = .9f))
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
            serie.seriePoints.minOfOrNull { it.offset.x } ?: 0f
        } ?: 0f
    val maxX =
        boundaries?.maxX ?: series.filterNot { it.seriePoints.isEmpty() }.maxOfOrNull { serie ->
            serie.seriePoints.maxOfOrNull { it.offset.x } ?: 0f
        } ?: 0f
    val minY =
        boundaries?.minY ?: series.filterNot { it.seriePoints.isEmpty() }.minOfOrNull { serie ->
            serie.seriePoints.minOfOrNull { it.offset.y } ?: 0f
        } ?: 0f
    val maxY =
        boundaries?.maxY ?: series.filterNot { it.seriePoints.isEmpty() }.maxOfOrNull { serie ->
            serie.seriePoints.maxOfOrNull { it.offset.y } ?: 0f
        } ?: 0f

    val vb = Boundaries(minX, maxX, minY, maxY)
    return vb
}

fun <E> DrawScope.drawSerie(
    points: List<DataPoint<E>>,
    color: Color,
    alpha: Float,
) {
    drawPoints(
        points.map { it.offset },
        PointMode.Polygon,
        color,
        3.dp.toPx(),
        StrokeCap.Round,
    )
    points.forEach { drawCircle(color = color, 4.dp.toPx(), it.offset, alpha) }
}

private fun <E> getSeriePoints(
    serie: Serie<E>,
    size: Size,
    scale: Offset,
    boundaries: Boundaries,
    scrollOffset: Offset,
    yOrientation: YOrientation,
): Serie<E> {
    val points = serie.seriePoints
        .map {
            it.copy(
                offset = getOnScreenPoint(
                    it,
                    size,
                    boundaries,
                    scrollOffset,
                    scale,
                    yOrientation
                )
            )
        }

    val pointsOffset = points.map { it.offset }
    val start = pointsOffset.lastOrNull { it.x <= 0.1f }
    val stop = pointsOffset.firstOrNull { it.x > 0.1f }
    val yOrigin = when {
        start?.y == stop?.y -> {
            start
        }
        start != null && stop != null -> {
            val fraction = start.x.absoluteValue / (stop.x - start.x)
            lerp(start, stop, fraction)
        }
        else -> {
            pointsOffset.firstOrNull { it.x in -0.1f..0.1f }
        }
    }
    return serie.copy(yOrigin = yOrigin, seriePoints = points)
}


private fun <E> getOnScreenPoint(
    dataPoint: DataPoint<E>,
    size: Size,
    boundaries: Boundaries,
    scrollOffset: Offset,
    scale: Offset,
    yOrientation: YOrientation
): Offset {
    val xFraction = boundaries.run { (dataPoint.offset.x - minX!!) / (maxX!! - minX) }
    val yFraction = boundaries.run { (dataPoint.offset.y - minY!!) / (maxY!! - minY) }
    val screenCenterX = size.width / 2f
    val screenCenterY = size.height / 2f
    val lerpX = lerp(-screenCenterX, screenCenterX, xFraction)
    val x = (lerpX + scrollOffset.x) * scale.x + screenCenterX
    val lerpY = when (yOrientation) {
        YOrientation.Down -> lerp(-screenCenterY, screenCenterY, yFraction)
        YOrientation.Up -> lerp(screenCenterY, -screenCenterY, yFraction)
    }
    val y = (lerpY + scrollOffset.y) * scale.y + screenCenterY
    return Offset(x, y)
}

data class Serie<E>(
    val seriePoints: List<DataPoint<E>>,
    val color: Color,
    val label: String,
    val yOrigin: Offset? = null
)


data class DataPoint<E>(val element: E?, val offset: Offset = Offset.Unspecified)

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
                DataPoint("TEST", Offset(it.toFloat(), Random.nextInt(20).toFloat()))
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
            yOrientation = YOrientation.Down,
            gridStep = Offset(5f, 1000f)
        )
    }
}

enum class YOrientation {
    Up, Down
}