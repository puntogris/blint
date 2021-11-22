package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.order.*

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
    }

    @Insert
    suspend fun insertOrderRecordsCrossRef(items: List<OrderRecordCrossRef>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record WHERE :productId = productId ORDER BY timestamp DESC")
    fun getProductRecordsPaged(productId: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND productName LIKE :productName ORDER BY timestamp DESC")
    fun getPagedSearch(productName: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :supplierId AND type = 'IN'")
    fun getSupplierRecords(supplierId: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND traderId = :clientId AND type = 'OUT'")
    fun getClientsRecords(clientId: String): PagingSource<Int, Record>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Orders INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY number DESC")
    fun getAllOrdersPaged(): PagingSource<Int, OrderWithRecords>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getAllOrderRecords(orderId: String): OrderWithRecords
}