package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puntogris.blint.model.Event

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    fun getAllPaged(): PagingSource<Int, Event>

}