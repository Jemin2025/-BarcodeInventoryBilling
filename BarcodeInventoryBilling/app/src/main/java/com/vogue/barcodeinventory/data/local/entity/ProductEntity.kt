package com.vogue.barcodeinventory.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["barcode"], unique = true),
        Index(value = ["category"]),
        Index(value = ["isActive"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Shirt, Pant, Bag, T-shirt, Accessories, etc.
    val sku: String, // Unique SKU e.g. SHR-00001
    val barcode: String, // Barcode value for Code 128 / EAN-13
    val purchasePrice: Double,
    val sellingPrice: Double,
    val mrp: Double? = null,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val size: String? = null,
    val color: String? = null,
    val brand: String? = null,
    val description: String? = null,
    val imageUri: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val estimatedProfitMargin: Double
        get() = if (sellingPrice > 0) ((sellingPrice - purchasePrice) / sellingPrice) * 100.0 else 0.0

    val isLowStock: Boolean
        get() = stockQuantity <= lowStockThreshold
}
