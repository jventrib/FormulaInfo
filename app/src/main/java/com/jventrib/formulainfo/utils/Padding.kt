package com.jventrib.formulainfo.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation

class Padding(
    private val top: Float = 0F,
    private val bottom: Float = 0F,
    private val left: Float = 0F,
    private val right: Float = 0F
) : Transformation {
    override fun key() = Padding::class.java.toString()

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
        input.pad(top, bottom, left, right)

    private fun Bitmap.pad(
        top: Float = 0F,
        bottom: Float = 0F,
        left: Float = 0F,
        right: Float = 0F
    ): Bitmap {
        val output = Bitmap.createBitmap(
            (width + left + right).toInt(),
            (height + top + bottom).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        canvas.drawBitmap(this, left, top, null)
        return output
    }
}
