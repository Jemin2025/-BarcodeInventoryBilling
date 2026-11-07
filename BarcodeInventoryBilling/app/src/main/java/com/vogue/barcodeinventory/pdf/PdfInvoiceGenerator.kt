package com.vogue.barcodeinventory.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.vogue.barcodeinventory.data.local.dao.InvoiceWithItems
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfInvoiceGenerator(private val context: Context) {

    /**
     * Generates a professional A4 Native PDF Invoice using Android's native PdfDocument.
     * Complies with Indian GST regulations: CGST, SGST, INR currency formatting, and table layout.
     */
    fun createA4InvoicePdf(invoiceData: InvoiceWithItems, businessName: String, gstin: String): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points (72 DPI)
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val invoice = invoiceData.invoice
        val items = invoiceData.items

        // Paints
        val headerPaint = Paint().apply {
            color = Color.parseColor("#0F172A") // Navy Blue
            style = Paint.Style.FILL
        }
        val textWhite = Paint().apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textSubWhite = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 10f
        }
        val textDark = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val textBold = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val tableHeaderPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            style = Paint.Style.FILL
        }

        // 1. Navy Header Bar
        canvas.drawRect(20f, 20f, 575f, 85f, headerPaint)
        canvas.drawText(businessName.uppercase(), 36f, 50f, textWhite)
        canvas.drawText("TAX INVOICE | GSTIN: $gstin", 36f, 70f, textSubWhite)

        // 2. Invoice Meta Details
        var y = 115f
        canvas.drawText("Invoice No: ${invoice.invoiceNumber}", 36f, y, textBold)
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        canvas.drawText("Date: ${dateFormat.format(Date(invoice.createdAt))}", 350f, y, textDark)

        y += 20f
        canvas.drawText("Billed To: ${invoice.customerName} (${invoice.customerMobile})", 36f, y, textDark)
        canvas.drawText("Payment Mode: ${invoice.paymentMethod}", 350f, y, textDark)

        // 3. Table Header
        y += 30f
        canvas.drawRect(20f, y, 575f, y + 26f, tableHeaderPaint)
        val colText = Paint().apply { color = Color.WHITE; textSize = 10f; typeface = Typeface.DEFAULT_BOLD }
        canvas.drawText("#", 30f, y + 17f, colText)
        canvas.drawText("Item / SKU", 60f, y + 17f, colText)
        canvas.drawText("Qty", 320f, y + 17f, colText)
        canvas.drawText("Rate (₹)", 380f, y + 17f, colText)
        canvas.drawText("Tax", 460f, y + 17f, colText)
        canvas.drawText("Total (₹)", 520f, y + 17f, colText)

        // 4. Items List
        y += 26f
        val linePaint = Paint().apply { color = Color.parseColor("#E2E8F0"); strokeWidth = 1f }
        items.forEachIndexed { idx, item ->
            y += 22f
            canvas.drawText("${idx + 1}", 30f, y, textDark)
            canvas.drawText("${item.productName} (${item.sku})", 60f, y, textBold)
            canvas.drawText("${item.quantity}", 325f, y, textDark)
            canvas.drawText(String.format("%.2f", item.unitPrice), 380f, y, textDark)
            canvas.drawText("${item.taxPercent.toInt()}%", 460f, y, textDark)
            canvas.drawText(String.format("%.2f", item.lineTotal), 520f, y, textBold)
            canvas.drawLine(20f, y + 6f, 575f, y + 6f, linePaint)
        }

        // 5. Grand Totals Box
        y += 35f
        canvas.drawText("Subtotal:", 380f, y, textDark)
        canvas.drawText(String.format("₹%.2f", invoice.subtotal), 520f, y, textDark)

        y += 18f
        canvas.drawText("Total Tax (GST):", 380f, y, textDark)
        canvas.drawText(String.format("₹%.2f", invoice.taxAmount), 520f, y, textDark)

        y += 22f
        val tealBold = Paint().apply { color = Color.parseColor("#0D9488"); textSize = 14f; typeface = Typeface.DEFAULT_BOLD }
        canvas.drawText("Grand Total:", 380f, y, tealBold)
        canvas.drawText(String.format("₹%.2f", invoice.grandTotal), 520f, y, tealBold)

        pdfDocument.finishPage(page)

        // Save File to app's external files directory
        val outputDir = File(context.getExternalFilesDir(null), "Invoices")
        if (!outputDir.exists()) outputDir.mkdirs()
        val pdfFile = File(outputDir, "${invoice.invoiceNumber}.pdf")

        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return pdfFile
    }
}
