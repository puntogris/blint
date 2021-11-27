package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("DELETE FROM event WHERE eventId = :eventId")
    suspend fun delete(eventId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp ASC")
    fun getEventsPaged(): PagingSource<Int, Event>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND status = :status ORDER BY timestamp ASC")
    fun getEventsWithStatusPaged(status: String): PagingSource<Int, Event>

    @Update
    suspend fun updateEvent(event: Event)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM event INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY timestamp ASC LIMIT 3")
    fun getLastThreeEventsFlow(): Flow<List<Event>>
}