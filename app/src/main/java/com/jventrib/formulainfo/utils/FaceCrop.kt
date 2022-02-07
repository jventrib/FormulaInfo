package com.jventrib.formulainfo.utils

import android.graphics.Bitmap
import android.graphics.Rect
import coil.bitmap.BitmapPool
import coil.size.PixelSize
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.max
import kotlin.math.min

class FaceCrop(private val faceBox: Rect, private val factor: Float = 1.3f) : Transformation {
    override fun key() = FaceCrop::class.java.toString()

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
        faceBox.scale(factor, PixelSize(input.width, input.height)).run {
            Bitmap.createBitmap(input, left, top, width(), height())
        }

    private fun Rect.scale(factor: Float, size: PixelSize): Rect {
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

    private fun Rect.bound(size: PixelSize) {
        this.left = max(this.left, 0)
        this.top = max(this.top, 0)
        this.right = min(this.right, size.width - 1)
        this.bottom = min(this.bottom, size.height - 1)
    }
}

