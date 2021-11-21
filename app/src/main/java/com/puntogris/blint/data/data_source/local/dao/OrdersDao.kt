package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.order.Debt
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.model.order.Order
import com.puntogris.blint.model.order.OrderRecordCrossRef
import com.puntogris.blint.model.order.OrderWithRecords
import com.puntogris.blint.model.order.Record
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

    @Transaction
    suspend fun insertOrder(order: OrderWithRecords) {
        insert(order.order)
        insert(order.records)

        insertOrderRecordsCrossRef(order.records.map {
            OrderRecordCrossRef(
                order.order.orderId,
                it.recordId
            )
        })
    }

    @Insert
    suspend fun insertOrderRecordsCrossRef(items: List<OrderRecordCrossRef>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getStatistics(): Statistic


    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record WHERE :productID = productId ORDER BY timestamp DESC")
    fun getProductRecordsPaged(productID: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND productName LIKE :productName ORDER BY timestamp DESC")
    fun getPagedSearch(productName: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :externalID AND type = 'IN'")
    fun getSupplierRecords(externalID: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :externalID AND type = 'OUT'")
    fun getClientsRecords(externalID: String): PagingSource<Int, Record>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Orders INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY number DESC")
    fun getAllOrdersPaged(): PagingSource<Int, OrderWithRecords>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getAllOrderRecords(orderId: String): OrderWithRecords
}