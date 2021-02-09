package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.*

@Dao
interface StatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statistic: Statistic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statistics: List<Statistic>)

    @Query("UPDATE statistic SET totalProducts = totalProducts + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun incrementTotalProducts()

    @Query("UPDATE statistic SET totalProducts = totalProducts - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun decrementTotalProducts()

    @Query("UPDATE statistic SET totalClients = totalClients + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun incrementTotalClients()

    @Query("UPDATE statistic SET totalClients = totalClients - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun decrementTotalClients()

    @Query("UPDATE statistic SET totalSuppliers = totalSuppliers + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun incrementTotalSuppliers()

    @Query("UPDATE statistic SET totalSuppliers = totalSuppliers - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")
    suspend fun decrementTotalSuppliers()

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getStatistics(): Statistic

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getAllClients(): List<Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getAllSuppliers(): List<Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' ORDER BY productID")
    suspend fun getAllRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND type = 'OUT' AND externalID != 0 ORDER BY externalName")
    suspend fun getAllClientsRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'  AND type = 'IN' AND externalID != 0 ORDER BY externalName ")
    suspend fun getAllSuppliersRecords(): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', :days) AND datetime('now', 'localtime') ORDER BY productID")
    suspend fun getRecordsWithDays(days:String): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId 
        WHERE id = '1' and date(timestamp, 'unixepoch','localtime')
        BETWEEN date(:startTime, 'unixepoch','localtime') AND  date(:endTime, 'unixepoch','localtime') 
        ORDER BY productID
     """
    )
    suspend fun getRecordsWithDaysFrame(startTime:Long, endTime: Long): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND type = 'OUT' AND externalID != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', :days) AND datetime('now', 'localtime') ORDER BY externalName")
    suspend fun getRecordsClientsWithDays(days:String): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId 
        WHERE id = '1' and date(timestamp, 'unixepoch','localtime')
        AND type = "OUT" AND externalID != 0
        BETWEEN date(:startTime, 'unixepoch','localtime') AND  date(:endTime, 'unixepoch','localtime') 
        ORDER BY externalName
     """
    )
    suspend fun getRecordsClientsWithDaysFrame(startTime:Long, endTime: Long): List<Record>


    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND type = 'IN' AND externalID != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', :days) AND datetime('now', 'localtime') ORDER BY externalName")
    suspend fun getRecordsSuppliersWithDays(days:String): List<Record>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM record INNER JOIN roomuser ON businessId = currentBusinessId 
        WHERE id = '1' and date(timestamp, 'unixepoch','localtime')
        AND type = 'IN' AND externalID != 0
        BETWEEN date(:startTime, 'unixepoch','localtime') AND  date(:endTime, 'unixepoch','localtime') 
        ORDER BY externalName
     """
    )
    suspend fun getRecordsSuppliersWithDaysFrame(startTime:Long, endTime: Long): List<Record>


}