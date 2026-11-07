package com.vogue.barcodeinventory.data.local.dao

import androidx.room.*
import com.vogue.barcodeinventory.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode OR sku = :barcode LIMIT 1")
    suspend fun findByBarcodeOrSku(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun findBySku(sku: String): ProductEntity?

    @Query("SELECT COUNT(*) FROM products WHERE category = :category")
    suspend fun getCategoryProductCount(category: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantityChange, updatedAt = :timestamp WHERE id = :productId")
    suspend fun adjustStockQuantity(productId: Long, quantityChange: Int, timestamp: Long)

    @Query("SELECT * FROM products WHERE stockQuantity <= lowStockThreshold AND isActive = 1")
    fun getLowStockProducts(): Flow<List<ProductEntity>>
}
