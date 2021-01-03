package com.puntogris.blint.data

import androidx.room.*
import com.puntogris.blint.model.Business

@Dao
interface BusinessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(business: Business)

    @Update
    suspend fun update(business: Business)

    @Query("SELECT * FROM business")
    suspend fun getBusinesses(): List<Business>

    @Query("SELECT COUNT(*) FROM business")
    suspend fun getCount(): Int


}