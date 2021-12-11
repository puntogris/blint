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
    fun getBusinessesFlow(): Flow<List<Business>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId AND localReferenceId = '1' LIMIT 1")
    suspend fun getCurrentBusiness(): Business

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getCurrentBusinessFlow(): Flow<Business>

    @Query("DELETE FROM business where businessId = :businessId")
    suspend fun deleteBusiness(businessId: String)

    @Query("UPDATE business SET ownerUid = :uid")
    suspend fun updateBusinessesOwnerUid(uid: String)

    @Query("UPDATE business SET totalOrders = totalOrders + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE business SET totalProducts = totalProducts + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalProducts()

    @Query("UPDATE business SET totalProducts = totalProducts - 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalProducts()

    @Query("UPDATE business SET totalTraders = totalTraders + 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalTraders()

    @Query("UPDATE business SET totalTraders = totalTraders - 1 WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalTraders()

    @Query("UPDATE business SET tradersDebt = :clientsDebt + tradersDebt WHERE businessId IN (SELECT businessId FROM business INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateTradersDebt(clientsDebt: Float)

}