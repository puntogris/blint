package com.puntogris.blint.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.model.Business

@Dao
interface BusinessDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(business: Business)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(businesses: List<Business>)

    @Query("DELETE FROM business")
    suspend fun deleteAll()

    @Transaction
    suspend fun syncEmployees(businesses: List<Business>){
        deleteAll()
        insert(businesses)
    }

    @Query("SELECT * FROM business")
    fun getBusiness(): List<Business>

    @Query("DELETE FROM business where businessId = :businessId")
    suspend fun deleteEmployeeWithBusinessId(businessId: Int)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT businessId FROM business")
    suspend fun getBusinessIdsList(): List<String>

    @Query("SELECT * FROM business WHERE businessId = :businessId LIMIT 1")
    suspend fun getEmployeeWithBusinessId(businessId: String): Business

}