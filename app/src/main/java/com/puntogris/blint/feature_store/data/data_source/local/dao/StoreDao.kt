package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(store: Store)

    @Query("SELECT * FROM store")
    suspend fun getStores(): List<Store>

    @Query("SELECT * FROM store")
    fun getStoresFlow(): Flow<List<Store>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM store INNER JOIN user ON currentStoreId = storeId AND localReferenceId = '1' LIMIT 1")
    suspend fun getCurrentStore(): Store

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1'")
    fun getCurrentStoreFlow(): Flow<Store>

    @Query("DELETE FROM store where storeId = :businessId")
    suspend fun deleteStore(businessId: String)

    @Query("UPDATE store SET ownerUid = :uid")
    suspend fun updateStoresOwnerUid(uid: String)

    @Query("UPDATE store SET totalOrders = totalOrders + 1 WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE store SET totalProducts = totalProducts + 1 WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalProducts()

    @Query("UPDATE store SET totalProducts = totalProducts - 1 WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalProducts()

    @Query("UPDATE store SET totalTraders = totalTraders + 1 WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalTraders()

    @Query("UPDATE store SET totalTraders = totalTraders - 1 WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalTraders()

    @Query("UPDATE store SET tradersDebt = :clientsDebt + tradersDebt WHERE storeId IN (SELECT storeId FROM store INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1')")
    suspend fun updateTradersDebt(clientsDebt: Float)

}