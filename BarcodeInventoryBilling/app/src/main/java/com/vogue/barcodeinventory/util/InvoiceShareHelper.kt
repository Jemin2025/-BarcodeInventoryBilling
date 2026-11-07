package com.vogue.barcodeinventory.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.vogue.barcodeinventory.data.local.entity.InvoiceEntity
import java.io.File

object InvoiceShareHelper {

    /**
     * Share invoice PDF via WhatsApp with pre-filled greeting message.
     * Uses Android FileProvider for secure URI granting and Android 11+ package queries.
     */
    fun shareInvoiceViaWhatsApp(context: Context, invoice: InvoiceEntity, pdfFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val cleanPhone = invoice.customerMobile.replace("[^0-9]".toRegex(), "")
        val prefilledMessage = "Hello ${invoice.customerName}, thank you for your purchase. " +
                "Please find your invoice ${invoice.invoiceNumber} attached. " +
                "Total Amount: ₹${String.format("%.2f", invoice.grandTotal)}."

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, prefilledMessage)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Target WhatsApp or WhatsApp Business
        val isWhatsAppInstalled = isPackageInstalled("com.whatsapp", context.packageManager) ||
                isPackageInstalled("com.whatsapp.w4b", context.packageManager)

        if (isWhatsAppInstalled) {
            shareIntent.setPackage(if (isPackageInstalled("com.whatsapp", context.packageManager)) "com.whatsapp" else "com.whatsapp.w4b")
            context.startActivity(shareIntent)
        } else {
            // Fallback to standard Android Sharesheet
            Toast.makeText(context, "WhatsApp not installed. Opening Android Sharesheet.", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
