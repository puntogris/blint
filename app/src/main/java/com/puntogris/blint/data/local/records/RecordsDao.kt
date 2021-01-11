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

    @Query("SELECT * FROM record")
    suspend fun getRecords(): List<Record>

    @Query("SELECT COUNT(*) FROM record")
    suspend fun getCount(): Int

    @Query("SELECT * FROM record")
    fun getAllPaged(): PagingSource<Int, Record>

    @Query("SELECT * FROM record WHERE :product = product")
    fun getProductRecordsPaged(product: Int): PagingSource<Int, Record>

}