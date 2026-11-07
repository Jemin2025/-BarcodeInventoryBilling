package com.vogue.barcodeinventory.data.local.dao

import androidx.room.*
import com.vogue.barcodeinventory.data.local.entity.InvoiceEntity
import com.vogue.barcodeinventory.data.local.entity.InvoiceItemEntity
import com.vogue.barcodeinventory.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>
)

@Dao
abstract class InvoiceDao {

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY id DESC")
    abstract fun getAllInvoicesWithItems(): Flow<List<InvoiceWithItems>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    abstract suspend fun getInvoiceWithItemsById(id: Long): InvoiceWithItems?

    @Insert
    abstract suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert
    abstract suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Insert
    abstract suspend fun insertStockMovements(movements: List<StockMovementEntity>)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :qty WHERE id = :productId")
    abstract suspend fun deductProductStock(productId: Long, qty: Int)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :qty WHERE id = :productId")
    abstract suspend fun restoreProductStock(productId: Long, qty: Int)

    @Query("UPDATE invoices SET isCancelled = 1, notes = :cancellationNote WHERE id = :invoiceId")
    abstract suspend fun markInvoiceCancelled(invoiceId: Long, cancellationNote: String)

    /**
     * ATOMIC INVOICE FINALIZATION TRANSACTION
     * Guarantees that invoice insertion, line items, stock deduction, and audit log
     * succeed together or roll back entirely.
     */
    @Transaction
    open suspend fun finalizeInvoiceTransaction(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>,
        stockMovements: List<StockMovementEntity>
    ): Long {
        val invoiceId = insertInvoice(invoice)
        val itemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        insertInvoiceItems(itemsWithId)

        // Deduct inventory quantities atomically
        for (item in items) {
            deductProductStock(item.productId, item.quantity)
        }

        // Record stock movements in audit ledger
        val movementsWithRef = stockMovements.map { it.copy(referenceId = invoice.invoiceNumber) }
        insertStockMovements(movementsWithRef)

        return invoiceId
    }

    /**
     * ATOMIC INVOICE RETURN & REVERSAL TRANSACTION
     */
    @Transaction
    open suspend fun cancelInvoiceTransaction(
        invoiceId: Long,
        items: List<InvoiceItemEntity>,
        reversalMovements: List<StockMovementEntity>,
        reason: String
    ) {
        markInvoiceCancelled(invoiceId, "Cancelled: $reason")
        for (item in items) {
            restoreProductStock(item.productId, item.quantity)
        }
        insertStockMovements(reversalMovements)
    }
}
