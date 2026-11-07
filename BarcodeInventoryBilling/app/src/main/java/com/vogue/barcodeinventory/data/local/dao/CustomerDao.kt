package com.vogue.barcodeinventory.data.local.dao

import androidx.room.*
import com.vogue.barcodeinventory.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name COLLATE NOCASE") fun getAllCustomers(): Flow<List<CustomerEntity>>
    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1") suspend fun getById(id: Long): CustomerEntity?
    @Query("SELECT * FROM customers WHERE mobile = :mobile LIMIT 1") suspend fun getByMobile(mobile: String): CustomerEntity?
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insert(customer: CustomerEntity): Long
    @Update suspend fun update(customer: CustomerEntity)
    @Delete suspend fun delete(customer: CustomerEntity)
}
