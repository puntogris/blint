package com.puntogris.blint.data.local.user

import androidx.room.*
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roomUser: RoomUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(firestoreUser: FirestoreUser)

    @Update
    suspend fun update(roomUser: RoomUser)

    @Query("SELECT * FROM roomuser")
    suspend fun getUser(): RoomUser


}