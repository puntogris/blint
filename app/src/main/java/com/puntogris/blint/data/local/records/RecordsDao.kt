package com.puntogris.blint.data.local.records

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product
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

    @Query("SELECT * FROM record WHERE supplierID = :supplierID")
    fun getSupplierRecords(supplierID:Int): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE clientID = :clientID")
    fun getClientsRecords(clientID:Int): PagingSource<Int, Record>
}