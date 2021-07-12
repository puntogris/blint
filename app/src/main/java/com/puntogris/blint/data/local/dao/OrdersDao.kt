package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.*

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: List<Record>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Transaction
    suspend fun insertOrderWithRecords(order: OrderWithRecords, recordsFinal: List<Record>){
        order.order.number = getStatistics().totalOrders + 1
        insert(order.order)
        insert(recordsFinal)
        recordsFinal.forEach {
            updateProductAmountWithType(it.productId, it.amount, order.order.type)
        }
        insertOrderRecordsCrossRef(recordsFinal.map { OrderRecordCrossRef(order.order.orderId, it.recordId) })
        incrementTotalOrders()
    }

    @Insert
    suspend fun insertOrderRecordsCrossRef(items: List<OrderRecordCrossRef>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getStatistics(): Statistic


    @Query("UPDATE statistic SET totalOrders = totalOrders + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE product SET amount = CASE WHEN :type = 'IN' THEN amount + :amount ELSE amount - :amount END WHERE productId = :id")
    suspend fun updateProductAmountWithType(id: String, amount: Int, type: String)

    @Update
    suspend fun update(record: Record)

    @Query("SELECT COUNT(*) FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getCount(): Int

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record WHERE :productID = productId ORDER BY timestamp DESC")
    fun getProductRecordsPaged(productID: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND productName LIKE :productName ORDER BY timestamp DESC")
    fun getPagedSearch(productName: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND traderId = :externalID AND type = 'IN'")
    fun getSupplierRecords(externalID: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND traderId = :externalID AND type = 'OUT'")
    fun getClientsRecords(externalID: String): PagingSource<Int, Record>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY number DESC")
    fun getAllOrdersPaged(): PagingSource<Int, OrderWithRecords>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getAllOrderRecords(orderId:String): OrderWithRecords
}