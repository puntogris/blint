package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Business
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(business: Business)

    @Query("SELECT * FROM business")
    suspend fun getBusiness(): List<Business>

    @Query("SELECT * FROM business")
    fun getBusinessFlow(): Flow<List<Business>>

    @Query("DELETE FROM business where businessId = :businessId")
    suspend fun deleteBusiness(businessId: String)

    @Query("UPDATE business SET ownerUid = :uid")
    suspend fun updateBusinessesOwnerUid(uid: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId AND localReferenceId = '1' LIMIT 1")
    suspend fun getCurrentBusiness(): Business

    @Query("UPDATE business SET totalOrders = totalOrders + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE business SET totalProducts = totalProducts + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalProducts()

    @Query("UPDATE business SET totalProducts = totalProducts - 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalProducts()

    @Query("UPDATE business SET totalClients = totalClients + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalClients()

    @Query("UPDATE business SET totalClients = totalClients - 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalClients()

    @Query("UPDATE business SET totalSuppliers = totalSuppliers + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalSuppliers()

    @Query("UPDATE business SET totalSuppliers = totalSuppliers - 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalSuppliers()

    @Query("UPDATE business SET clientsDebt = :clientsDebt + clientsDebt WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateClientsDebt(clientsDebt: Float)

    @Query("UPDATE business SET suppliersDebt = :suppliersDebt + suppliersDebt WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateSupplierDebt(suppliersDebt: Float)
}