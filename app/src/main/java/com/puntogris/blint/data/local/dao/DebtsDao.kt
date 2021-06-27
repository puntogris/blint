package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt

@Dao
interface DebtsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt)

    @Query("SELECT * FROM debt WHERE traderId = :traderId ORDER BY timestamp DESC LIMIT 5")
    suspend fun getDebtsWithId(traderId: String): List<Debt>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getDebtsForBusiness(): LiveData<BusinessDebtsData>

    @Query("UPDATE employee SET clientsDebt = :clientsDebt + clientsDebt WHERE businessId IN (SELECT businessId FROM employee INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1')")
    suspend fun updateClientsDebt(clientsDebt: Float)

    @Query("UPDATE employee SET suppliersDebt = :suppliersDebt + suppliersDebt WHERE businessId IN (SELECT businessId FROM employee INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1')")
    suspend fun updateSupplierDebt(suppliersDebt: Float)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM debt INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp DESC")
    fun getPagedDebts(): PagingSource<Int, Debt>

}