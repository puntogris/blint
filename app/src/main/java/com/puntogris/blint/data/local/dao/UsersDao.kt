package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.puntogris.blint.model.RoomUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roomUser: RoomUser)

    @Transaction
    suspend fun checkAndCreateUser(roomUser: RoomUser){
        if (userExists() == null) {
            insert(roomUser)
        }
    }

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    suspend fun userExists(): RoomUser?

    @Update
    suspend fun update(roomUser: RoomUser)

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    suspend fun getUser(): RoomUser

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    fun getUserLiveData(): LiveData<RoomUser>

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    fun getUserFlow(): Flow<RoomUser>

    @Query("UPDATE roomuser SET currentBusinessId = :id, currentBusinessName = :name, currentBusinessType = :type, currentBusinessOwner = :owner, currentUid = :currentUid, currentBusinessStatus = :status WHERE userId = '1' ")
    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner:String, currentUid: String, status:String)

    @Query("UPDATE roomuser SET username= :name, country = :country WHERE userId = '1'")
    suspend fun updateUserNameCountry(name:String, country: String)
}
