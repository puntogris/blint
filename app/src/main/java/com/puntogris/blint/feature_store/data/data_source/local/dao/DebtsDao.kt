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

    @Query("UPDATE statistic SET clientsDebt = :clientsDebt + clientsDebt WHERE businessId IN (SELECT businessId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateTotalClientsDebt(clientsDebt: Float)

    @Query("UPDATE statistic SET suppliersDebt = :suppliersDebt + suppliersDebt WHERE businessId IN (SELECT businessId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateTotalSupplierDebt(suppliersDebt: Float)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM debt INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp DESC")
    fun getDebtsPaged(): PagingSource<Int, Debt>

    @Query("UPDATE client SET debt = debt + :amount WHERE clientId = :clientId")
    suspend fun updateClientDebt(clientId: String, amount: Float)

    @Query("UPDATE supplier SET debt = debt + :amount WHERE supplierId = :supplierId")
    suspend fun updateTotalSupplierDebt(supplierId: String, amount: Float)

}