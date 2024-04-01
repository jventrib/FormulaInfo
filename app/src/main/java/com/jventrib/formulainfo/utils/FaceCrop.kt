package com.jventrib.formulainfo.utils

import android.graphics.Bitmap
import android.graphics.Rect
import coil.size.Size
import coil.size.pxOrElse
import coil.transform.Transformation
import kotlin.math.max
import kotlin.math.min

class FaceCrop(private val faceBox: Rect, private val factor: Float = 1.3f) : Transformation {
    override val cacheKey: String
        get() = FaceCrop::class.java.toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap =
        faceBox.scale(factor, Size(input.width, input.height)).run {
            Bitmap.createBitmap(input, left, top, width(), height())
        }

    private fun Rect.scale(factor: Float, size: Size): Rect {
        val diffHorizontal = (right - left) * (factor - 1f)
        val diffVertical = (bottom - top) * (factor - 1f)

        top -= (diffVertical / 2f).toInt()
        bottom += (diffVertical / 2f).toInt()

        left -= (diffHorizontal / 2f).toInt()
        right += (diffHorizontal / 2f).toInt()
        val rect = Rect(left, top, right, bottom)
        rect.bound(size)
        return rect
    }

    private fun Rect.bound(size: Size) {
        this.left = max(this.left, 0)
        this.top = max(this.top, 0)
        this.right = min(this.right, size.width.pxOrElse { 0 } - 1)
        this.bottom = min(this.bottom, size.height.pxOrElse { 0 } - 1)
    }
}
