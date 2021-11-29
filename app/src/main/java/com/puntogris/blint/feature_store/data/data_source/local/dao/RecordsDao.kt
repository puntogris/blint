package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.order.Record

@Dao
interface RecordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: List<Record>)

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

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'OUT' AND traderId != 0 ORDER BY traderName")
    suspend fun getClientsRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'  AND type = 'IN' AND traderId != 0 ORDER BY traderName ")
    suspend fun getSuppliersRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'OUT' AND traderId != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', -:days) AND datetime('now', 'localtime') ORDER BY traderName")
    suspend fun getClientsRecordsTimeframe(days: Int): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'IN' AND traderId != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', -:days) AND datetime('now', 'localtime') ORDER BY traderName")
    suspend fun getSuppliersRecordsTimeframe(days: Int): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND productId = :productId AND date(timestamp, 'unixepoch','localtime') >= datetime('now', -:days) ORDER BY timestamp ASC LIMIT 1")
    suspend fun getProductsRecordsTimeFrame(productId: String, days: Int): Record

}