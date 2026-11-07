package com.vogue.barcodeinventory.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.vogue.barcodeinventory.BarcodeInventoryApp
import com.vogue.barcodeinventory.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val db by lazy { (application as BarcodeInventoryApp).database }
    private lateinit var list: LinearLayout
    private lateinit var summary: TextView
    private val money = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(20, 24, 20, 16); setBackgroundColor(0xFFF2F2F7.toInt()) }
        val title = TextView(this).apply { text = "Barcode Inventory & Billing"; textSize = 23f; setTextColor(0xFF0F172A.toInt()); gravity = Gravity.CENTER_VERTICAL; setPadding(4, 8, 4, 8) }
        root.addView(title)
        summary = TextView(this).apply { text = "Your stock at a glance"; textSize = 15f; setTextColor(0xFF475569.toInt()); setPadding(4, 8, 4, 16) }
        root.addView(summary)
        val add = MaterialButton(this).apply { text = "＋  Add Product"; setOnClickListener { showAddProductDialog() } }
        root.addView(add, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val heading = TextView(this).apply { text = "PRODUCTS"; textSize = 13f; setTextColor(0xFF64748B.toInt()); setPadding(4, 22, 4, 8) }
        root.addView(heading)
        val scroll = ScrollView(this)
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        setContentView(root)
        lifecycleScope.launch { db.productDao().getAllProducts().collectLatest { products ->
            summary.text = "${products.size} products  •  ${products.sumOf { it.stockQuantity }} units in stock"
            list.removeAllViews()
            if (products.isEmpty()) { list.addView(TextView(this@MainActivity).apply { text = "No products yet. Tap Add Product to create your first item."; textSize = 15f; setPadding(12, 24, 12, 24); setTextColor(0xFF64748B.toInt()) }) }
            products.forEach { product -> list.addView(productCard(product)) }
        } }
    }

    private fun productCard(p: ProductEntity): android.view.View {
        val card = MaterialCardView(this).apply { radius = 18f; cardElevation = 2f; setCardBackgroundColor(-1); useCompatPadding = true }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(16, 12, 16, 12) }
        box.addView(TextView(this).apply { text = p.name; textSize = 17f; setTextColor(0xFF0F172A.toInt()) })
        box.addView(TextView(this).apply { text = "SKU: ${p.sku}   •   Barcode: ${p.barcode}"; textSize = 12f; setTextColor(0xFF64748B.toInt()) })
        box.addView(TextView(this).apply { text = "Stock: ${p.stockQuantity}     Selling: ${money.format(p.sellingPrice)}"; textSize = 14f; setPadding(0, 8, 0, 0); setTextColor(0xFF0F766E.toInt()) })
        card.addView(box); card.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 10 }; return card
    }

    private fun showAddProductDialog() {
        val fields = listOf("Product name", "Category", "SKU", "Barcode", "Purchase price", "Selling price", "Opening stock")
        val inputs = fields.map { label -> EditText(this).apply { hint = label; singleLine = true; setPadding(24, 12, 24, 12) } }
        val container = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(20, 4, 20, 0); inputs.forEach { addView(it) } }
        AlertDialog.Builder(this).setTitle("Add product").setView(ScrollView(this).apply { addView(container) })
            .setNegativeButton("Cancel", null).setPositiveButton("Save", null).create().also { dialog ->
                dialog.setOnShowListener { dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val name = inputs[0].text.toString().trim(); val category = inputs[1].text.toString().trim().ifBlank { "General" }; val sku = inputs[2].text.toString().trim(); val barcode = inputs[3].text.toString().trim().ifBlank { sku }
                    val purchase = inputs[4].text.toString().toDoubleOrNull(); val selling = inputs[5].text.toString().toDoubleOrNull(); val stock = inputs[6].text.toString().toIntOrNull()
                    if (name.isBlank() || sku.isBlank() || purchase == null || selling == null || stock == null || stock < 0) { Toast.makeText(this, "Enter name, SKU, valid prices and stock", Toast.LENGTH_LONG).show(); return@setOnClickListener }
                    lifecycleScope.launch { try { db.productDao().insertProduct(ProductEntity(name = name, category = category, sku = sku, barcode = barcode, purchasePrice = purchase, sellingPrice = selling, stockQuantity = stock)); dialog.dismiss() } catch (e: Exception) { Toast.makeText(this@MainActivity, "Could not save: ${e.localizedMessage ?: "Check duplicate SKU/barcode"}", Toast.LENGTH_LONG).show() } }
                } }
                dialog.show()
            }
    }
}
