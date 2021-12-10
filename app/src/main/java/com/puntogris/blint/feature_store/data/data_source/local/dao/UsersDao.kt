package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puntogris.blint.feature_store.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT uid FROM user WHERE localReferenceId = '1'")
    suspend fun getUserId(): String

    @Query("SELECT * FROM user WHERE localReferenceId = '1'")
    fun getUserFlow(): Flow<User>

    @Query("SELECT * FROM user WHERE localReferenceId = '1'")
    suspend fun getUser(): User

    @Query("SELECT currentBusinessId FROM user WHERE localReferenceId = '1'")
    suspend fun getCurrentBusinessId(): String

    @Query("UPDATE user SET currentBusinessId = :businessId WHERE localReferenceId = '1'")
    suspend fun updateCurrentBusiness(businessId: String)
}


