package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.Business

@Dao
interface BusinessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(business: Business)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(businesses: List<Business>)

    @Query("DELETE FROM business")
    suspend fun deleteAll()

    @Update
    suspend fun update(business: Business)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM business INNER JOIN roomuser ON userID = currentUid WHERE id = '1'")
    suspend fun getBusinesses(): List<Business>

    @Query("SELECT COUNT(*) FROM business")
    suspend fun getCount(): Int

}