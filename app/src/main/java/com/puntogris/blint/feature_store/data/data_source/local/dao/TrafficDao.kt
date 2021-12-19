package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Traffic
import kotlinx.coroutines.flow.Flow

@Dao
interface TrafficDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(traffic: Traffic)

    @Update
    suspend fun update(traffic: Traffic)

    @Query("SELECT * FROM traffic WHERE date = :date")
    suspend fun getTraffic(date: String): Traffic?

    @Query("SELECT * FROM traffic LIMIT :days")
    suspend fun getTraffic(days: Int): List<Traffic>

    @Query("SELECT * FROM traffic ORDER BY date DESC LIMIT :days")
    fun getTrafficFlow(days: Int): Flow<List<Traffic>>

    @Transaction
    suspend fun updateOrInsert(traffic: Traffic) {
        val todayTraffic = getTraffic(traffic.date)
        if (todayTraffic == null) {
            insert(traffic)
        } else {
            traffic.apply {
                sales += todayTraffic.sales
                purchases += todayTraffic.purchases
            }
            insert(traffic)
        }
    }

}