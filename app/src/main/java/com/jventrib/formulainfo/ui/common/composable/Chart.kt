package com.jventrib.formulainfo.ui.common.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.google.android.material.math.MathUtils.lerp
import com.jventrib.formulainfo.ui.common.abs
import com.jventrib.formulainfo.ui.common.coerceAtMost
import com.jventrib.formulainfo.ui.common.coerceIn
import com.jventrib.formulainfo.ui.common.detectTransformGesturesXY
import com.jventrib.formulainfo.ui.common.div
import com.jventrib.formulainfo.ui.common.formatDecimal
import com.jventrib.formulainfo.ui.common.times
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
    customDraw: DrawScope.(List<Serie<E>>) -> Unit = {},
    xLabelTransform: (Float) -> String = { it.formatDecimal(false) },
    yLabelTransform: (Float) -> String = { it.formatDecimal(false) }
) {
    if (series.isEmpty()) return
    val axisColor = colors.onBackground
    val backgroundColor = colors.background
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

        // Coerce scrollOffset at each recomposition, so zoom out always keep inside boundaries
        scrollOffset = scrollOffset.let {
            Offset(
                it.x.coerceIn(
                    -screenCenterX + screenCenterX / scale.x,
                    screenCenterX - screenCenterX / scale.x
                ),
                it.y.coerceIn(
                    -screenCenterY + screenCenterY / scale.y,
                    screenCenterY - screenCenterY / scale.y
                )
            )
        }
        val actualBoundaries = getBoundaries(boundaries, series)

        val allSeriesSize = series.maxOfOrNull { it.seriePoints.size } ?: 1
        val pointAlpha = (10 * (scale.getDistance() - 1) / allSeriesSize).coerceIn(0f, 1f)

        val state = ChartState(
            series,
            size,
            actualBoundaries,
            scrollOffset,
            scale,
            yOrientation,
            gridStep,
            allSeriesSize
        )

        val onScreenSeries = series.map { serie ->
            getSeriePoints(serie, state)
        }

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
                // Draw Grid
                drawGrid(state)

                // Series
                onScreenSeries.forEach { serieScreen ->
                    drawSerie(serieScreen.seriePoints, serieScreen.color, pointAlpha)
                }

                drawAxisLabels(
                    axisColor = axisColor,
                    backgroundColor = backgroundColor,
                    state = state,
                    xLabelTransform,
                    yLabelTransform
                )

                // custom
                customDraw(onScreenSeries)
            }
        }
    }
}

@Composable
private fun <E> YAxis(seriesPoints: List<Serie<E>>) {
    Box(
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
                    color = Color.White,
                    modifier = Modifier
                        .offset(offset = {
                            IntOffset(0, yOrigin.y.roundToInt() - 12.dp.roundToPx())
                        })
                        .clip(MaterialTheme.shapes.medium)
                        .background(serie.color)
                )
            } else null
        }
    }
}

private fun <E> DrawScope.drawGrid(state: ChartState<E>) {
    // Compute grid coord
    if (state.series.isNotEmpty() && state.gridStep != null) {

        val verticalPadding = 16.dp.toPx() * 2
        val horizontalPadding = 4.dp.toPx()

        val xRange =
            range(state.boundaries.minX, state.boundaries.maxX, state.gridStep.x)

        xRange.forEach { x ->
            val onScreenPoint = getOnScreenPoint(Offset(x, 0f), state).copy(y = 0f)
            if (size.toRect().contains(onScreenPoint)) {
                drawLine(
                    Color.LightGray,
                    start = Offset(onScreenPoint.x, -verticalPadding),
                    end = Offset(
                        onScreenPoint.x,
                        this.size.height + verticalPadding
                    )
                )
            }
        }

        val yRange =
            range(state.boundaries.minY, state.boundaries.maxY, state.gridStep.y)

        yRange.forEach { y ->
            val onScreenPoint = getOnScreenPoint(Offset(0f, y), state).copy(x = 0f)
            if (size.toRect().contains(onScreenPoint)) {
                drawLine(
                    Color.LightGray,
                    start = Offset(-horizontalPadding, onScreenPoint.y),
                    end = Offset(
                        this.size.width + horizontalPadding,
                        onScreenPoint.y
                    )
                )
            }
        }
    }
}

private fun <E> DrawScope.drawAxisLabels(
    axisColor: Color,
    backgroundColor: Color,
    state: ChartState<E>,
    xLabelTransform: (Float) -> String,
    yLabelTransform: (Float) -> String
) {
    val axisLabelPaint = Paint().asFrameworkPaint().apply {
        textSize = 32f
        color = axisColor.toArgb()
    }

    state.gridStep?.let { gridStep ->
        val xRange =
            range(state.boundaries.minX, state.boundaries.maxX, gridStep.x)

        xRange.forEach { x ->
            val label = xLabelTransform(x)
            val onScreenPoint = getOnScreenPoint(Offset(x, 0f), state).copy(y = 0f)
            if (size.toRect().contains(onScreenPoint)) {
                drawRoundRect(
                    backgroundColor,
                    onScreenPoint.copy(x = onScreenPoint.x - 8.dp.toPx(), y = this.size.height),
                    Size(label.length * 24f, 40f),
                    CornerRadius(12f, 12f),
                    alpha = 0.7f
                )
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        label,
                        onScreenPoint.x - 8 * label.length, // Center Axis Label on grid line
                        this.size.height + 12.dp.toPx(),
                        axisLabelPaint
                    )
                }
            }
        }

        val yRange =
            range(state.boundaries.minY, state.boundaries.maxY, gridStep.y)

        yRange.forEach { y ->
            val onScreenPoint = getOnScreenPoint(Offset(0f, y), state).copy(x = 0f)
            if (size.toRect().contains(onScreenPoint)) {
                val label = yLabelTransform(y)
                drawRoundRect(
                    backgroundColor,
                    onScreenPoint.copy(x = -8f, y = onScreenPoint.y - 20f),
                    Size(20f + label.length * 16f, 40f),
                    CornerRadius(12f, 12f),
                    alpha = 0.7f
                )
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        label,
                        0f,
                        onScreenPoint.y + 10, // Center Axis Label on grid line
                        axisLabelPaint
                    )
                }
            }
        }
    }
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

private fun <E> getSeriePoints(serie: Serie<E>, state: ChartState<E>): Serie<E> {
    val points = serie.seriePoints
        .map {
            it.copy(
                offset = getOnScreenPoint(
                    it.offset,
                    state
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

private fun <E> getOnScreenPoint(offset: Offset, state: ChartState<E>): Offset {
    return state.run {
        val xFraction = boundaries.run { (offset.x - minX) / (maxX - minX) }
        val yFraction = boundaries.run { (offset.y - minY) / (maxY - minY) }
        val screenCenterX = size.width / 2f
        val screenCenterY = size.height / 2f
        val lerpX = lerp(-screenCenterX, screenCenterX, xFraction)
        val x = (lerpX + scrollOffset.x) * scale.x + screenCenterX
        val lerpY = when (yOrientation) {
            YOrientation.Down -> lerp(-screenCenterY, screenCenterY, yFraction)
            YOrientation.Up -> lerp(screenCenterY, -screenCenterY, yFraction)
        }
        val y = (lerpY + scrollOffset.y) * scale.y + screenCenterY
        Offset(x, y)
    }
}

private fun <E> getBoundaries(
    boundaries: Boundaries?,
    series: List<Serie<E>>
): ActualBoundaries {
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

    return ActualBoundaries(minX, maxX, minY, maxY)
}

private fun range(from: Float, to: Float, step: Float): Sequence<Float> {
    return generateSequence(from) { it + step }.takeWhile { it <= to }
}

enum class YOrientation {
    Up, Down
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

data class ActualBoundaries(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
)

data class ChartState<E>(
    val series: List<Serie<E>>,
    val size: Size,
    val boundaries: ActualBoundaries,
    val scrollOffset: Offset,
    val scale: Offset,
    val yOrientation: YOrientation,
    val gridStep: Offset?,
    val allSeriesSize: Int
)

@Preview(showSystemUi = false)
@Composable
fun ChartPreview() {
    val series = (0..5).map {
        Serie(
            (0..10).map {
                DataPoint("TEST", Offset(it.toFloat(), Random.nextInt(20).toFloat()))
            },
            Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
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
            gridStep = Offset(5f, 1000f),
        )
    }
}
