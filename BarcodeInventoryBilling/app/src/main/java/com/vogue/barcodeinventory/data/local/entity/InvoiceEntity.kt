package com.vogue.barcodeinventory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    indices = [
        Index(value = ["invoiceNumber"], unique = true),
        Index(value = ["customerId"]),
        Index(value = ["createdAt"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String, // INV-2026-0001
    val customerId: Long? = null,
    val customerName: String,
    val customerMobile: String,
    val customerGstin: String? = null,
    val subtotal: Double,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val grandTotal: Double,
    val amountPaid: Double,
    val balanceDue: Double,
    val paymentMethod: String, // Cash, UPI, Card, Bank Transfer
    val paymentStatus: String, // Paid, Partially Paid, Unpaid
    val pdfFilePath: String? = null,
    val notes: String? = null,
    val isCancelled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"]), Index(value = ["productId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val productId: Long,
    val productName: String,
    val sku: String,
    val barcode: String,
    val quantity: Int,
    val unitPrice: Double,
    val purchaseCost: Double,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxPercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val lineTotal: Double
)
