package com.vogue.barcodeinventory.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [Index(value = ["mobile"], unique = true)]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mobile: String, // +91 ...
    val email: String? = null,
    val address: String? = null,
    val gstin: String? = null,
    val totalPurchases: Double = 0.0,
    val outstandingBalance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "stock_movements",
    indices = [Index(value = ["productId"]), Index(value = ["timestamp"])]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val sku: String,
    val movementType: String, // Opening Stock, Purchase, Sale, Sale Return, Manual Adjustment, Damaged
    val quantityChanged: Int, // e.g. -2 for sale, +10 for purchase
    val previousStock: Int,
    val newStock: Int,
    val referenceId: String? = null, // Invoice number or PO number
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
