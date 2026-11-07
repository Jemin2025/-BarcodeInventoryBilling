package com.vogue.barcodeinventory.data.local.dao

import androidx.room.*
import com.vogue.barcodeinventory.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC") fun getAllMovements(): Flow<List<StockMovementEntity>>
    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY timestamp DESC") fun getForProduct(productId: Long): Flow<List<StockMovementEntity>>
    @Insert suspend fun insert(movement: StockMovementEntity): Long
    @Insert suspend fun insertAll(movements: List<StockMovementEntity>)
}
