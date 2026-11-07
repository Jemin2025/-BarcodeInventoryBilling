package com.vogue.barcodeinventory.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_QR_CODE
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    // Debounce & Scan-Lock mechanism to prevent multiple accidental reads from same camera frame
    private var lastScannedCode: String? = null
    private var lastScannedTimestamp: Long = 0
    private val DEBOUNCE_DELAY_MS = 1500L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { rawValue ->
                            val currentTime = System.currentTimeMillis()
                            if (rawValue != lastScannedCode || (currentTime - lastScannedTimestamp) > DEBOUNCE_DELAY_MS) {
                                lastScannedCode = rawValue
                                lastScannedTimestamp = currentTime
                                onBarcodeDetected(rawValue)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Non-fatal scanning log
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
