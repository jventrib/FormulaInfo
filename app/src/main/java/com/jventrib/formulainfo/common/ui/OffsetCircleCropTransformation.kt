@file:Suppress("unused")

package com.jventrib.formulainfo.common.ui

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.applyCanvas
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.min

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 */
class OffsetCircleCropTransformation : Transformation {

    override fun key(): String = OffsetCircleCropTransformation::class.java.name

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val minSize = min(input.width, input.height)
        val radius = minSize / 2f
        val output = pool.get(minSize, minSize, input.safeConfig)
        output.applyCanvas {
            drawCircle(radius, radius, radius, paint)
            paint.xfermode = XFERMODE
            drawBitmap(input, radius - input.width / 2f, radius - input.height / 2f + 12, paint)
        }

        return output
    }

    override fun equals(other: Any?) = other is OffsetCircleCropTransformation

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "CircleCropTransformation()"

    private val Bitmap.safeConfig: Bitmap.Config
        get() = config ?: Bitmap.Config.ARGB_8888

    private companion object {
        val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
}
