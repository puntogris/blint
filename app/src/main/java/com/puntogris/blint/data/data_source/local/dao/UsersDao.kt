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
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId AND userId = '1'LIMIT 1")
    suspend fun getCurrentBusinessFromUser(): Business

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN user ON currentBusinessId = businessId WHERE userId = '1' LIMIT 1")
    fun getUserFlow(): Flow<Business>

    @Query("UPDATE user SET currentBusinessId = :businessId WHERE userId = '1' ")
    suspend fun updateUserCurrentBusiness(businessId: String)
}


