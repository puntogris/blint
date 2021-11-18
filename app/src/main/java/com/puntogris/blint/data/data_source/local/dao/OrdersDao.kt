package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.CLIENT
import kotlin.math.absoluteValue

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: List<Record>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt)

    @Query("UPDATE client SET debt = debt + :amount WHERE clientId = :clientId")
    suspend fun updateClientDebt(clientId: Int, amount: Float)

    @Query("UPDATE supplier SET debt = debt + :amount WHERE supplierId = :supplierId")
    suspend fun updateSupplierDebt(supplierId: Int, amount: Float)

    @Transaction
    suspend fun insertOrderWithRecords(order: OrderWithRecords, recordsFinal: List<Record>) {
        if (order.debt != null) {
            val debt = Debt(
                orderId = order.order.orderId,
                debtId = order.debt!!.debtId,
                amount = order.debt!!.amount,
                traderId = order.order.traderId,
                traderName = order.order.traderName,
                author = order.order.author,
                businessId = order.order.businessId,
                type = order.debt!!.type
            )
            if (debt.type == CLIENT) {
                updateClientsDebt(debt.amount)
                updateClientDebt(debt.traderId, debt.amount)
            } else {
                updateSupplierDebt(debt.amount)
                updateSupplierDebt(debt.traderId, debt.amount)
            }
            insert(debt)
        }
        order.order.number = getStatistics().totalOrders + 1
        insert(order.order)
        insert(recordsFinal)
        recordsFinal.forEach {
            updateProductAmountWithType(it.productId, it.amount.absoluteValue, order.order.type)
            if (it.type == "IN") updateProductTotalInStock(it.productId, it.amount.absoluteValue)
            else updateProductTotalOutStock(it.productId, it.amount.absoluteValue)
        }
        insertOrderRecordsCrossRef(recordsFinal.map {
            OrderRecordCrossRef(
                order.order.orderId,
                it.recordId
            )
        })
        incrementTotalOrders()
    }

    @Query("UPDATE product SET totalInStock = :amount + totalInStock WHERE productId = :id")
    suspend fun updateProductTotalInStock(id: Int, amount: Int)

    @Query("UPDATE product SET totalOutStock = :amount + totalOutStock WHERE productId = :id")
    suspend fun updateProductTotalOutStock(id: Int, amount: Int)

    @Query("UPDATE statistic SET clientsDebt = :clientsDebt + clientsDebt WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateClientsDebt(clientsDebt: Float)

    @Query("UPDATE statistic SET suppliersDebt = :suppliersDebt + suppliersDebt WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateSupplierDebt(suppliersDebt: Float)

    @Insert
    suspend fun insertOrderRecordsCrossRef(items: List<OrderRecordCrossRef>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getStatistics(): Statistic

    @Query("UPDATE statistic SET totalOrders = totalOrders + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE product SET amount = CASE WHEN :type = 'IN' THEN amount + :amount ELSE amount - :amount END WHERE productId = :id")
    suspend fun updateProductAmountWithType(id: Int, amount: Int, type: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record WHERE :productID = productId ORDER BY timestamp DESC")
    fun getProductRecordsPaged(productID: Int): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND productName LIKE :productName ORDER BY timestamp DESC")
    fun getPagedSearch(productName: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :externalID AND type = 'IN'")
    fun getSupplierRecords(externalID: Int): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :externalID AND type = 'OUT'")
    fun getClientsRecords(externalID: Int): PagingSource<Int, Record>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Orders INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY number DESC")
    fun getAllOrdersPaged(): PagingSource<Int, OrderWithRecords>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getAllOrderRecords(orderId: String): OrderWithRecords
}