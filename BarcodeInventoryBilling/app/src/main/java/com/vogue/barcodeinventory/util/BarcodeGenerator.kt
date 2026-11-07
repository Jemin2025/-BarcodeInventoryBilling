package com.vogue.barcodeinventory.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.*

object BarcodeGenerator {

    /**
     * Generates a high-resolution barcode Bitmap using ZXing.
     * Supports Code 128 (standard internal inventory) and EAN-13 (GS1 products).
     * Includes required quiet zones and maximum contrast for 100% reliable scanning.
     */
    fun createBarcodeBitmap(
        content: String,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        widthPx: Int = 600,
        heightPx: Int = 200
    ): Bitmap {
        val writer = MultiFormatWriter()
        val hints = EnumMap<com.google.zxing.EncodeHintType, Any>(com.google.zxing.EncodeHintType::class.java).apply {
            put(com.google.zxing.EncodeHintType.MARGIN, 10) // Quiet zone margin
            put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        val bitMatrix: BitMatrix = writer.encode(content, format, widthPx, heightPx, hints)
        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height

        val pixels = IntArray(matrixWidth * matrixHeight)
        for (y in 0 until matrixHeight) {
            val offset = y * matrixWidth
            for (x in 0 until matrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
        return bitmap
    }

    /**
     * EAN-13 Checksum validator
     */
    fun isValidEan13(code: String): Boolean {
        if (code.length != 13 || !code.all { it.isDigit() }) return false
        val digits = code.map { it.toString().toInt() }
        var sum = 0
        for (i in 0 until 12) {
            sum += if (i % 2 == 0) digits[i] else digits[i] * 3
        }
        val check = (10 - (sum % 10)) % 10
        return check == digits[12]
    }
}
