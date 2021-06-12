package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.Debt

@Dao
interface DebtsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt)

    @Query("SELECT * FROM debt WHERE traderId = :traderId ORDER BY timestamp DESC LIMIT 5")
    suspend fun getDebtsWithId(traderId: Int): List<Debt>

}