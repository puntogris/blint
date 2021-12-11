package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.order.Debt

@Dao
interface DebtsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt)

    @Query("SELECT * FROM debt WHERE traderId = :traderId ORDER BY timestamp DESC")
    fun getTraderDebtsPaged(traderId: String): PagingSource<Int, Debt>

    @Query("UPDATE store SET tradersDebt = :clientsDebt + tradersDebt WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1')")
    suspend fun updateTotalTradersDebt(clientsDebt: Float)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM debt INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getDebtsPaged(): PagingSource<Int, Debt>

    @Query("UPDATE trader SET debt = debt + :amount WHERE traderId = :traderId")
    suspend fun updateTraderDebt(traderId: String, amount: Float)
}