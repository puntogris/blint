package com.puntogris.blint.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId AND localReferenceId = '1'LIMIT 1")
    suspend fun getCurrentBusinessFromUser(): Business

    @Query("SELECT currentBusinessId FROM user WHERE localReferenceId = '1'")
    suspend fun getCurrentBusinessId(): String

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId WHERE localReferenceId = '1' LIMIT 1")
    fun getUserFlow(): Flow<Business>

    @Query("UPDATE user SET currentBusinessId = :businessId WHERE localReferenceId = '1' ")
    suspend fun updateUserCurrentBusiness(businessId: String)
}


