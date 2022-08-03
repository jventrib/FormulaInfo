package com.jventrib.formulainfo.ui.common.composable

import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.material.math.MathUtils.lerp
import com.jventrib.formulainfo.ui.common.detectTransformGesturesXY
import com.jventrib.formulainfo.ui.common.formatDecimal
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
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

    Row {
        // YAxis(onScreenSeries)
        BoxWithConstraints(
            Modifier
                .padding(vertical = 16.dp, horizontal = 4.dp)
                .fillMaxSize()
        ) {
            val chartState = rememberChartState<E>()
            LaunchedEffect(series) {
                chartState.init(series, this@BoxWithConstraints, boundaries, yOrientation)
            }
            Box {
                Canvas(
                    modifier = modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGesturesXY(onGesture = chartState.onGesture)
                        }
                ) {
                    chartState.paint.strokeWidth = 3.dp.toPx()
                    // Draw Grid
                    // drawGrid(state)

                    // Series
                    chartState.series.forEach { serie ->
                        drawSerie(serie, chartState)
                    }

                    // drawAxisLabels(
                    //     axisColor = axisColor,
                    //     backgroundColor = backgroundColor,
                    //     state = state,
                    //     xLabelTransform,
                    //     yLabelTransform
                    // )
                    //
                    // // custom
                    // customDraw(onScreenSeries)
                }
            }
        }
    }
}

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

private fun <E> DrawScope.drawGrid(state: ChartStateOld<E>) {
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
    state: ChartStateOld<E>,
    xLabelTransform: (Float) -> String,
    yLabelTransform: (Float) -> String
) {
    val axisLabelPaint = Paint().apply {
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
    serie: Serie<E>,
    chartState: ChartState<E>,
) {
    chartState.paint.color = serie.color.toArgb()

    if (serie.mappedPoints.isNotEmpty()) {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            drawLines(nativeCanvas, serie, 0)
            drawLines(nativeCanvas, serie, 2)

            // chartState.paint.alpha = chartState.alpha
            for (i in serie.mappedPoints.indices step 2) {
                if (i >= 2 && i < serie.mappedPoints.size - 3) {
                    val distPrev = sqrt(
                        (serie.mappedPoints[i] - serie.mappedPoints[i - 2]).pow(2) +
                            (serie.mappedPoints[i + 1] - serie.mappedPoints[i - 1]).pow(2)
                    )
                    val distNext = sqrt(
                        (serie.mappedPoints[i] - serie.mappedPoints[i + 2]).pow(2) +
                            (serie.mappedPoints[i + 1] - serie.mappedPoints[i + 3]).pow(2)
                    )
                    val alpha = distPrev + distNext - 50f
                    chartState.paint.alpha = alpha.toInt().coerceIn(0, 255)
                } else {
                    chartState.paint.alpha = 255
                }

                nativeCanvas.drawCircle(
                    serie.mappedPoints[i],
                    serie.mappedPoints[i + 1],
                    4.dp.toPx(),
                    chartState.paint
                )
            }
        }
    }
}

private fun <E> DrawScope.drawLines(
    nativeCanvas: NativeCanvas,
    serie: Serie<E>,
    offset: Int
) {
    nativeCanvas.drawLines(
        serie.mappedPoints,
        offset,
        serie.mappedPoints.size - offset,
        Paint().apply {
            color = serie.color.toArgb()
            strokeWidth = 3.dp.toPx()
            flags = ANTI_ALIAS_FLAG
            alpha = 255
        }
    )
}

private fun <E> DrawScope.getPaint(serie: Serie<E>) {
    Paint().apply {
        val shaderString = ""
        // shader = RuntimeShader(shaderString)
        color = serie.color.toArgb()
        strokeWidth = 3.dp.toPx()
        flags = ANTI_ALIAS_FLAG
        // pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    }
}

private fun <E> getSeriePoints(serie: Serie<E>, state: ChartStateOld<E>): Serie<E> {
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

private fun <E> getOnScreenPoint(offset: Offset, state: ChartStateOld<E>): Offset {
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

private fun range(from: Float, to: Float, step: Float): Sequence<Float> {
    return generateSequence(from) { it + step }.takeWhile { it <= to }
}

data class Serie<E>(
    val seriePoints: List<DataPoint<E>>,
    val color: Color,
    val label: String,
    val yOrigin: Offset? = null
) {
    val points = FloatArray(seriePoints.size * 2)
    val mappedPoints = FloatArray(seriePoints.size * 2)
}

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

data class ChartStateOld<E>(
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
        val color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
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
