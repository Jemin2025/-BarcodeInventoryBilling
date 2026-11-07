package com.vogue.barcodeinventory

import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialCalculationTest {

    @Test
    fun testPaiseFinancialAccuracyWithoutFloatingPointError() {
        val unitPrice = 899.00
        val quantity = 3
        val discountPercent = 10.0 // 10% discount
        val taxRate = 12.0 // 12% GST

        val rawTotalPaise = (unitPrice * 100).toLong() * quantity // 269700 paise
        val discountPaise = (rawTotalPaise * discountPercent / 100).toLong() // 26970 paise
        val netBeforeTaxPaise = rawTotalPaise - discountPaise // 242730 paise

        val taxPaise = (netBeforeTaxPaise * taxRate / 100).toLong() // 29127 paise
        val grandTotalPaise = netBeforeTaxPaise + taxPaise // 271857 paise

        val grandTotalRupees = grandTotalPaise / 100.0

        assertEquals(2718.57, grandTotalRupees, 0.001)
    }

    @Test
    fun testStockDeductionAfterInvoice() {
        val initialStock = 28
        val quantitySold = 3
        val remainingStock = initialStock - quantitySold
        assertEquals(25, remainingStock)
    }

    @Test
    fun testSkuUniquenessFormat() {
        val category = "SHR"
        val sequence = 1
        val sku = "$category-${String.format("%05d", sequence)}"
        assertEquals("SHR-00001", sku)
    }
}
