package com.puntogris.blint.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

typealias ResultListener = (result : String) -> Unit

class BarcodeAnalyzer @Inject constructor() : ImageAnalysis.Analyzer {

    private var isScanning: Boolean = false
    private val scanner = BarcodeScanning.getClient()

    private var resultListener: ResultListener? = null

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            isScanning = true
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        val rawValue = barcode.rawValue
                        rawValue?.let {
                            resultListener?.invoke(it)
                        }
                    }
                    isScanning = false
                    imageProxy.close()
                }

                .addOnFailureListener {
                    isScanning = false
                    imageProxy.close()
                }
        }
    }
    fun onResult(resultListener: ResultListener){
        this.resultListener = resultListener
    }
}