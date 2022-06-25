package com.jventrib.formulainfo.ui.common.composable

import android.graphics.Matrix
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.jventrib.formulainfo.ui.common.div
import com.jventrib.formulainfo.ui.common.times
import logcat.LogPriority
import logcat.logcat

class ChartState<E> {
    var series by mutableStateOf<List<Serie<E>>>(listOf(), neverEqualPolicy())

    // private val allSeriesSize = series.maxOfOrNull { it.seriePoints.size } ?: 1
    // val pointAlpha = (10 * (scale.getDistance() - 1) / allSeriesSize).coerceIn(0f, 1f)
    val onGesture: (centroid: Offset, pan: Offset, zoom: Offset, rotation: Float) -> Unit =
        { _, offsetChange, zoomChange, rotationChange ->
            val scale = zoomChange
            // abs(Offset(1f, 1f) * zoomChange.coerceAtMost(Offset(2f, 2f))).coerceIn(
            //     Offset(1f, 1f), Offset(50f, 25f)
            // )
            val scrollOffset = offsetChange / scale
            matrix.postScale(scale.x, scale.y)
            matrix.postTranslate(scrollOffset.x, scrollOffset.y)
            logcat(LogPriority.VERBOSE) { "matrix: $matrix" }
            transformSeries()
        }
    private val matrix: Matrix = Matrix()

    private fun transformSeries() {
        series.forEach { matrix.mapPoints(it.mappedPoints, it.points) }
        series = series
    }

    fun init(
        series: List<Serie<E>>,
        box: BoxWithConstraintsScope,
        boundaries: Boundaries?,
        yOrientation: YOrientation
    ) {
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
            val size = Size(box.constraints.maxWidth.toFloat(), box.constraints.maxHeight.toFloat())
            val actualBoundaries = getBoundaries(boundaries, this@ChartState.series)
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
        transformSeries()
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
