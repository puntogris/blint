package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Record

@Dao
interface RecordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Update
    suspend fun update(record: Record)

    @Query("SELECT COUNT(*) FROM record")
    suspend fun getCount(): Int

    @Query("SELECT * FROM record ORDER BY timestamp DESC")
    fun getAllPaged(): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE :productID = productID ORDER BY timestamp DESC")
    fun getProductRecordsPaged(productID: Int): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE productName LIKE :productName ORDER BY timestamp DESC")
    fun getPagedSearch(productName: String): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE externalID = :externalID AND type = 'IN'")
    fun getSupplierRecords(externalID:Int): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE externalID = :externalID AND type = 'OUT'")
    fun getClientsRecords(externalID:Int): PagingSource<Int, Record>
}