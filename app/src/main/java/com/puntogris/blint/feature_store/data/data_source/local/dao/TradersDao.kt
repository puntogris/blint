package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Trader

@Dao
interface TradersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trader: Trader)

    @Query("DELETE FROM trader WHERE traderId = :traderId")
    suspend fun delete(traderId: String)

    @Query("SELECT * FROM trader WHERE traderId = :traderId")
    suspend fun getTrader(traderId: String): Trader

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM trader INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1'")
    suspend fun getTraders(): List<Trader>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM trader INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1'")
    fun getTradersPaged(): PagingSource<Int, Trader>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM trader c INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND c.name LIKE ('%'|| :query ||'%')")
    fun getTradersSearchPaged(query: String): PagingSource<Int, Trader>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM trader c INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND c.name LIKE ('%'|| :query ||'%') and type = :filter")
    fun getTradersSearchPaged(query: String, filter: String): PagingSource<Int, Trader>

    @Query("UPDATE trader SET debt = debt + :amount WHERE traderId = :traderId")
    suspend fun updateTraderDebt(traderId: String, amount: Float)
}