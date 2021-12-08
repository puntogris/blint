package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.order.Order
import com.puntogris.blint.feature_store.domain.model.order.OrderRecordCrossRef
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Insert
    suspend fun insertOrderRecordsCrossRef(items: List<OrderRecordCrossRef>)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Orders INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY number DESC")
    fun getAllOrdersPaged(): PagingSource<Int, OrderWithRecords>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getAllOrderRecords(orderId: String): OrderWithRecords
}