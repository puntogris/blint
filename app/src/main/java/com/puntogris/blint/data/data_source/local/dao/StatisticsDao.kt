package com.puntogris.blint.data.data_source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductRecordExcel
import kotlinx.coroutines.flow.Flow
import kotlin.math.absoluteValue


@Dao
interface StatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statistic: Statistic)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getCurrentBusinessStatistics(): LiveData<Statistic>

    @Query("UPDATE statistic SET totalOrders = totalOrders + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalOrders()

    @Query("UPDATE statistic SET totalProducts = totalProducts + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalProducts()

    @Query("UPDATE statistic SET totalProducts = totalProducts - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalProducts()

    @Query("UPDATE statistic SET totalClients = totalClients + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalClients()

    @Query("UPDATE statistic SET totalClients = totalClients - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalClients()

    @Query("UPDATE statistic SET totalSuppliers = totalSuppliers + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun incrementTotalSuppliers()

    @Query("UPDATE statistic SET totalSuppliers = totalSuppliers - 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1') ")
    suspend fun decrementTotalSuppliers()

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getStatistics(): Statistic

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getBusinessStatisticsFlow(): Flow<Statistic>

    @Query("UPDATE statistic SET clientsDebt = :clientsDebt + clientsDebt WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateClientsDebt(clientsDebt: Float)

    @Query("UPDATE statistic SET suppliersDebt = :suppliersDebt + suppliersDebt WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1')")
    suspend fun updateSupplierDebt(suppliersDebt: Float)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getAllProducts(): List<Product>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND productId = :productId AND date(timestamp, 'unixepoch','localtime') >= datetime('now', :days) ORDER BY timestamp ASC LIMIT 1")
    suspend fun getRecordsWithDays(productId: String, days: String): Record

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM product INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getProductRecordExcelList(): List<ProductRecordExcel>

    @Transaction
    suspend fun getProductRecordDaysExcelList(days: String): List<ProductRecordExcel> {
        val list = mutableListOf<ProductRecordExcel>()
        getAllProducts().forEach {
            val data = getRecordsWithDays(it.productId, days)
            list.add(
                ProductRecordExcel(
                    it.name,
                    if (data.type == "IN") it.historicInStock - (data.totalInStock - data.amount).absoluteValue
                    else it.historicInStock - data.totalInStock,
                    if (data.type == "OUT") it.historicOutStock - (data.totalOutStock - data.amount).absoluteValue
                    else it.historicOutStock - data.totalOutStock
                )
            )
        }
        return list
    }

//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'OUT' AND traderId != 0 ORDER BY traderName")
//    suspend fun getAllClientsRecords(): List<ClientRecordExcel>
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'  AND type = 'IN' AND traderId != 0 ORDER BY traderName ")
//    suspend fun getAllSuppliersRecords(): List<SupplierRecordExcel>
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'OUT' AND traderId != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', :days) AND datetime('now', 'localtime') ORDER BY traderName")
//    suspend fun getRecordsClientsWithDays(days:String): List<ClientRecordExcel>
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query(
//        """
//        SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId
//        WHERE localReferenceId = '1' and date(timestamp, 'unixepoch','localtime')
//        AND type = "OUT" AND traderId != 0
//        BETWEEN date(:startTime, 'unixepoch','localtime') AND  date(:endTime, 'unixepoch','localtime')
//        ORDER BY traderName
//     """
//    )
//    suspend fun getRecordsClientsWithDaysFrame(startTime:Long, endTime: Long): List<ClientRecordExcel>
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND type = 'IN' AND traderId != 0 and date(timestamp, 'unixepoch','localtime') BETWEEN datetime('now', :days) AND datetime('now', 'localtime') ORDER BY traderName")
//    suspend fun getRecordsSuppliersWithDays(days:String): List<SupplierRecordExcel>
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query(
//        """
//        SELECT * FROM record INNER JOIN user ON businessId = currentBusinessId
//        WHERE localReferenceId = '1' and date(timestamp, 'unixepoch','localtime')
//        AND type = 'IN' AND traderId != 0
//        BETWEEN date(:startTime, 'unixepoch','localtime') AND  date(:endTime, 'unixepoch','localtime')
//        ORDER BY traderName
//     """
//    )
//    suspend fun getRecordsSuppliersWithDaysFrame(startTime:Long, endTime: Long): List<SupplierRecordExcel>
}