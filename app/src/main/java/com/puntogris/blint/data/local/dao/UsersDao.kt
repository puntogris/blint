package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user WHERE userId = '1' ")
    suspend fun getUser(): User

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN user WHERE userId = '1' AND currentBusinessId = businessId LIMIT 1")
    suspend fun getCurrentBusiness(): Employee

    @Query("SELECT * FROM user WHERE userId = '1' ")
    fun getUserLiveData(): LiveData<User>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN user WHERE userId = '1' AND currentBusinessId = businessId LIMIT 1")
    fun getUserFlow(): Flow<Employee>

    @Query("UPDATE user SET currentBusinessId = :id WHERE userId = '1' ")
    suspend fun updateCurrentBusiness(id:String)

    @Query("UPDATE user SET username = :name, country = :country WHERE userId = '1'")
    suspend fun updateUserNameCountry(name:String, country: String)
}
