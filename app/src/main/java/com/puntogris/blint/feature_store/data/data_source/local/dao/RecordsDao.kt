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
    @Query("SELECT * FROM record INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND traderId = :traderId")
    fun getTradersRecords(traderId: String): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getAllRecordsPaged(): PagingSource<Int, Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND traderId != '' ORDER BY traderName")
    suspend fun getTradersRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND traderId != '' AND datetime(timestamp) BETWEEN datetime('now',  '-'|| :days || ' days') AND datetime('now') ORDER BY traderName")
    suspend fun getTraderRecordsTimeframe(days: Int): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND productId = :productId AND datetime(timestamp) >= datetime('now',  '-'|| :days || ' days') ORDER BY timestamp ASC LIMIT 1")
    suspend fun getProductsRecordsTimeFrame(productId: String, days: Int): Record?
}