package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Event

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("DELETE FROM event WHERE eventId = :eventId")
    suspend fun delete(eventId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp ASC")
    fun getAllPaged(): PagingSource<Int, Event>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' AND status = :status ORDER BY timestamp ASC")
    fun getPagedEventsWithFilter(status: String): PagingSource<Int, Event>

    @Update
    suspend fun updateEvent(event: Event)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp ASC LIMIT 3")
    fun getLastThreeEvents():List<Event>
}