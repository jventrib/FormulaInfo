package com.jventrib.formulainfo.ui.common.composable

import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import logcat.LogPriority
import logcat.logcat

class ChartState<E> {
    var series by mutableStateOf<List<Serie<E>>>(listOf(), neverEqualPolicy())
    val paint = Paint().apply { flags = Paint.ANTI_ALIAS_FLAG }
    var alpha = 0
    private val matrix: Matrix = Matrix()
    private val offsetMatrix: Matrix = Matrix()

    private val initialValues = FloatArray(9)
    private val currentValues = FloatArray(9)
    private lateinit var yOrientation: YOrientation
    private var size: Size = Size.Zero
    private var bottomTranslateY: Float = 0.0f
    private var topTranslateY: Float = 0.0f
    private var leftTranslateX: Float = 0.0f
    private var rightTranslateX: Float = 0.0f
    private lateinit var actualBoundaries: ActualBoundaries

    fun init(
        series: List<Serie<E>>,
        box: BoxWithConstraintsScope,
        boundaries: Boundaries?,
        yOrientation: YOrientation
    ) {
        this.yOrientation = yOrientation
        this.series = series
        this.series.forEach {
            it.seriePoints.forEachIndexed { index, dataPoint ->
                it.points[index * 2] = dataPoint.offset.x
                it.points[index * 2 + 1] = dataPoint.offset.y
            }
        }.apply {
            logcat("seriesState") { "Init Points done" }
        }

        matrix.apply {
            size = Size(box.constraints.maxWidth.toFloat(), box.constraints.maxHeight.toFloat())
            actualBoundaries = getBoundaries(boundaries, this@ChartState.series)
            val xFraction = actualBoundaries.run { size.width / (maxX - minX) }
            val yFraction = actualBoundaries.run { size.height / (maxY - minY) }
            reset()
            if (yOrientation == YOrientation.Up) {
                postScale(xFraction, -yFraction)
                postTranslate(0f, size.height)
            } else {
                postScale(xFraction, yFraction)
            }
        }
        matrix.getValues(initialValues)
        transformSeries()
    }

    private val allSeriesSize = series.maxOfOrNull { it.seriePoints.size } ?: 1
    val onGesture: (centroid: Offset, pan: Offset, zoom: Offset, rotation: Float) -> Unit =
        { centroid, offsetChange, zoomChange, rotationChange ->

            // /!\ No memory allocation in this function
            matrix.getValues(currentValues)
            val scaleX = initialValues[Matrix.MSCALE_X] / currentValues[Matrix.MSCALE_X]
            val scaleY = initialValues[Matrix.MSCALE_Y] / currentValues[Matrix.MSCALE_Y]

            matrix.postTranslate(-centroid.x, -centroid.y)
            matrix.postScale(
                zoomChange.x.coerceAtLeast(scaleX),
                zoomChange.y.coerceAtLeast(scaleY)
            )
            matrix.postTranslate(centroid.x, centroid.y)

            rightTranslateX = -currentValues[Matrix.MTRANS_X]
            leftTranslateX = rightTranslateX + size.width - size.width / scaleX

            bottomTranslateY = initialValues[Matrix.MTRANS_Y] - currentValues[Matrix.MTRANS_Y]
            logcat(LogPriority.VERBOSE) { "bottomTranslateY: $bottomTranslateY" }
            topTranslateY =
                if (yOrientation == YOrientation.Down) bottomTranslateY + size.height - size.height / scaleY
                else bottomTranslateY - size.height + size.height / scaleY

            matrix.postTranslate(
                offsetChange.x.coerceAtLeast(leftTranslateX).coerceAtMost(rightTranslateX),
                if (yOrientation == YOrientation.Down)
                    offsetChange.y.coerceAtLeast(topTranslateY).coerceAtMost(bottomTranslateY)
                else
                    offsetChange.y.coerceAtLeast(bottomTranslateY).coerceAtMost(topTranslateY)
            )
            val scale = Offset(1 / scaleX, 1 / scaleY).getDistance()
            alpha = ((10 * (scale - 1) / allSeriesSize) * 2).coerceIn(0f, 255f).toInt()
            logcat { "alpha: $alpha" }
            transformSeries()
        }

    private fun transformSeries() {
        logcat(LogPriority.VERBOSE) { "matrix: $matrix" }
        offsetMatrix.set(matrix)
        offsetMatrix.preTranslate(-actualBoundaries.minX, -actualBoundaries.minY)

        series.forEach { offsetMatrix.mapPoints(it.mappedPoints, it.points) }
        series = series
    }
}

@Composable
fun <E> rememberChartState() = remember { ChartState<E>() }

enum class YOrientation {
    Up, Down
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
