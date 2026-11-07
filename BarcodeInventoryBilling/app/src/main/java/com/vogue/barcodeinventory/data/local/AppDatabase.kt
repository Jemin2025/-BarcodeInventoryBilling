package com.vogue.barcodeinventory.data.local

import android.content.Context
import androidx.room.*
import com.vogue.barcodeinventory.data.local.dao.*
import com.vogue.barcodeinventory.data.local.entity.*

@Database(
    entities = [
        ProductEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        CustomerEntity::class,
        StockMovementEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun customerDao(): CustomerDao
    abstract fun stockMovementDao(): StockMovementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "barcode_inventory_billing.db"
                )
                .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
