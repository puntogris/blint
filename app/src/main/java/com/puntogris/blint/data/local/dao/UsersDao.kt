package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roomUser: RoomUser)

    @Update
    suspend fun update(roomUser: RoomUser)

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    suspend fun getUser(): RoomUser

    @Query("SELECT * FROM roomuser WHERE userId = '1' ")
    fun getUserLiveData(): LiveData<RoomUser>

    @Query("UPDATE roomuser SET currentBusinessId = :id, currentBusinessName = :name, currentBusinessType = :type WHERE userId = '1' ")
    suspend fun updateCurrentBusiness(id:String, name:String, type:String)

}
