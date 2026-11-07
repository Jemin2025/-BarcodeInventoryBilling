package com.vogue.barcodeinventory

import android.app.Application
import com.vogue.barcodeinventory.data.local.AppDatabase

class BarcodeInventoryApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
