package com.jventrib.formulainfo.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.hypot
import kotlinx.coroutines.suspendCancellableCoroutine
import logcat.logcat

object FaceDetection {

    private var detector: FaceDetector? = null

    private fun initDetector() {
        // Initialize FaceDetectorOptions
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        // Get a face detector.
        detector = FaceDetection.getClient(highAccuracyOpts)
    }

    fun close() {
        detector?.close()
        detector = null
    }

    suspend fun detect(input: Bitmap): Rect? {
        // Put the input image into MLKIt's InputImage format.
        val image = InputImage.fromBitmap(input, 0)

        // Run the facial detection algorithm to detect all the faces in the image.
        return suspendCancellableCoroutine { continuation ->
            if (detector == null) {
                initDetector()
            }
            detector!!.process(image).addOnSuccessListener { faces ->
//            detector.close()
                val imageCenter = Point(input.width / 2, input.height / 2)

                val mainFace = faces.minByOrNull {
                    val distX = it.boundingBox.centerX() - imageCenter.x
                    val distY = it.boundingBox.centerY() - imageCenter.y
                    hypot(distX.toDouble(), distY.toDouble())
                }

                continuation.resume(mainFace?.boundingBox)
            }.addOnFailureListener { e ->
                // Log the exception so we can figure out what's going wrong.
                logcat("FaceDetection") { "detection: ERROR: $e" }
                continuation.resumeWithException(e)
            }
        }
    }
}
