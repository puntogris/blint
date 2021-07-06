package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.Timestamp
import com.puntogris.blint.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("DELETE FROM event WHERE eventId = :eventId")
    suspend fun delete(eventId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp ASC")
    fun getAllPaged(): PagingSource<Int, Event>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND date(timestamp, 'unixepoch','localtime') = date(:timestamp, 'unixepoch','localtime') ORDER BY timestamp ASC")
    fun getDayEvents(timestamp:Timestamp): PagingSource<Int, Event>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND status = :status ORDER BY timestamp ASC")
    fun getPagedEventsWithFilter(status: String): PagingSource<Int, Event>

    @Update
    suspend fun updateEvent(event: Event)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp ASC LIMIT 3")
    fun getLastThreeEvents():List<Event>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY timestamp ASC LIMIT 3")
    fun getLastThreeEventsFlow(): Flow<List<Event>>

    @Query("SELECT COUNT(*) FROM event INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getCount() :Int
}