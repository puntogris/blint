package com.puntogris.blint.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.model.Business
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
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId AND localReferenceId = '1'LIMIT 1")
    suspend fun getCurrentBusiness(): Business
}