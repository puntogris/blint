package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roomUser: RoomUser)

    @Update
    suspend fun update(roomUser: RoomUser)

    @Query("SELECT * FROM roomuser WHERE id = '1' ")
    suspend fun getUser(): RoomUser

}