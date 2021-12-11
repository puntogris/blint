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

    @Query("UPDATE business SET tradersDebt = :clientsDebt + tradersDebt WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateTotalTradersDebt(clientsDebt: Float)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM debt INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getDebtsPaged(): PagingSource<Int, Debt>

    @Query("UPDATE trader SET debt = debt + :amount WHERE traderId = :traderId")
    suspend fun updateTraderDebt(traderId: String, amount: Float)
}