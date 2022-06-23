package com.jventrib.formulainfo.ui.common.composable

import android.graphics.Matrix
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.jventrib.formulainfo.ui.common.abs
import com.jventrib.formulainfo.ui.common.coerceAtMost
import com.jventrib.formulainfo.ui.common.coerceIn
import com.jventrib.formulainfo.ui.common.div
import com.jventrib.formulainfo.ui.common.times
import logcat.logcat

class ChartState<E>(
    seriesState: MutableState<List<Serie<E>>>,
    matrixState: MutableState<Matrix>
) {
    var series by seriesState
    private var scrollOffset = Offset.Zero
    private var scale by mutableStateOf(Offset(1f, 1f))
    private val allSeriesSize = series.maxOfOrNull { it.seriePoints.size } ?: 1

    private var matrix by matrixState
    val pointAlpha = (10 * (scale.getDistance() - 1) / allSeriesSize).coerceIn(0f, 1f)
    val onGesture: (centroid: Offset, pan: Offset, zoom: Offset, rotation: Float) -> Unit =
        { _, offsetChange, zoomChange, rotationChange ->
            scale =
                abs(Offset(1f, 1f) * zoomChange.coerceAtMost(Offset(2f, 2f))).coerceIn(
                    Offset(1f, 1f), Offset(50f, 25f)
                )
            scrollOffset = offsetChange / scale
            matrix.postTranslate(scrollOffset.x, scrollOffset.y)
            matrix.postScale(scale.x, scale.y)
            matrix = matrix
            logcat { "matrix: $matrix" }
            transformSeries()
        }

    init {
        series.forEach {
            it.seriePoints.forEachIndexed { index, dataPoint ->
                it.points[index * 2] = dataPoint.offset.x
                it.points[index * 2 + 1] = dataPoint.offset.y
            }
        }.apply {
            logcat("seriesState") { "Init Points done" }
        }
        transformSeries()
    }

    private fun transformSeries() {
        series.forEach {
            matrix.mapPoints(it.mappedPoints, it.points)
        }
        series = series
    }
}

@Composable
fun <E> rememberChartState(
    box: BoxWithConstraintsScope,
    boundaries: Boundaries?,
    series: List<Serie<E>>,
    orientation: YOrientation,
    seriesState: MutableState<List<Serie<E>>> = remember {
        mutableStateOf(
            series,
            neverEqualPolicy()
        )
    },
    matrixState: MutableState<Matrix> = remember { mutableStateOf(Matrix(), neverEqualPolicy()) }
) = remember(series) {
    ChartState(
        seriesState.apply { value = series },
        matrixState.init(box, boundaries, series, orientation)
    )
}

private fun <E> MutableState<Matrix>.init(
    box: BoxWithConstraintsScope,
    boundaries: Boundaries?,
    series: List<Serie<E>>,
    orientation: YOrientation
) = apply {
    value.apply {
        val size = Size(box.constraints.maxWidth.toFloat(), box.constraints.maxHeight.toFloat())
        val actualBoundaries = getBoundaries(boundaries, series)
        val xFraction = actualBoundaries.run { size.width / (maxX - minX) }
        val yFraction = actualBoundaries.run { size.height / (maxY - minY) }
        reset()
        if (orientation == YOrientation.Up) {
            postScale(xFraction, -yFraction)
            postTranslate(0f, size.height)
        } else {
            postScale(xFraction, yFraction)
        }
    }
}

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
