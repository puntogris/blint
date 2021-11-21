package com.puntogris.blint.data.repository.statistics

import androidx.lifecycle.LiveData
import com.puntogris.blint.data.data_source.local.dao.ClientsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.SuppliersDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductRecordExcel
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RepoResult
import kotlinx.coroutines.withContext
import javax.inject.Inject


class StatisticRepository @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : IStatisticRepository {

    override suspend fun getAllClients(): RepoResult<List<Client>> = withContext(dispatcher.io) {
        try {
            val data = clientsDao.getClients()
            RepoResult.Success(data)
        } catch (e: Exception) {
            RepoResult.Error()
        }
    }

    override suspend fun getAllProducts(): RepoResult<List<Product>> = withContext(dispatcher.io) {
        try {
            val data = statisticsDao.getAllProducts()
            RepoResult.Success(data)
        } catch (e: Exception) {
            RepoResult.Error()
        }
    }

    override fun getCurrentBusinessStatistics(): LiveData<Statistic> {
        return statisticsDao.getCurrentBusinessStatistics()
    }


    override suspend fun getAllSuppliers(): RepoResult<List<Supplier>> =
        withContext(dispatcher.io) {
            try {
                val data = suppliersDao.getSuppliers()
                RepoResult.Success(data)
            } catch (e: Exception) {
                RepoResult.Error()
            }
        }

    override suspend fun getBusinessCounters(): RepoResult<Statistic> =
        withContext(dispatcher.io) {
            try {
                val data = statisticsDao.getStatistics()
                RepoResult.Success(data)
            } catch (e: Exception) {
                RepoResult.Error()
            }
        }

    override suspend fun getProductsReports(timeCode: String): RepoResult<List<ProductRecordExcel>> =
        withContext(dispatcher.io) {
            try {
                val data = when (timeCode) {
                    "WEEKLY" -> statisticsDao.getProductRecordDaysExcelList("-7 days")
                    "MONTHLY" -> statisticsDao.getProductRecordDaysExcelList("-30 days")
                    "QUARTERLY" -> statisticsDao.getProductRecordDaysExcelList("-90 days")
                    "BIANNUAL" -> statisticsDao.getProductRecordDaysExcelList("-180 days")
                    "ANNUAL" -> statisticsDao.getProductRecordDaysExcelList("-360 days")
                    else -> statisticsDao.getProductRecordExcelList()
                }
                RepoResult.Success(data)
            } catch (e: Exception) {
                RepoResult.Error()
            }
        }

//    override suspend fun getClientsReports(timeCode: String, startTime:Long, endTime:Long): RepoResult<List<ClientRecordExcel>> {
//        return try {
//            val data = when(timeCode){
//                "WEEKLY" -> statisticsDao.getRecordsClientsWithDays("-7 days")
//                "MONTHLY" -> statisticsDao.getRecordsClientsWithDays("-30 days")
//                "QUARTERLY" -> statisticsDao.getRecordsClientsWithDays("-90 days")
//                "BIANNUAL" -> statisticsDao.getRecordsClientsWithDays("-180 days")
//                "ANNUAL" -> statisticsDao.getRecordsClientsWithDays("-360 days")
//                "HISTORICAL" -> statisticsDao.getAllClientsRecords()
//                else -> statisticsDao.getRecordsClientsWithDaysFrame(startTime, endTime)
//            }
//            RepoResult.Success(data)
//        }catch (e:Exception){
//            RepoResult.Error(e)
//        }
//    }
//
//    override suspend fun getSuppliersReports(timeCode: String, startTime:Long, endTime:Long): RepoResult<List<SupplierRecordExcel>> {
//        return try {
//            val data =when(timeCode){
//                "WEEKLY" -> statisticsDao.getRecordsSuppliersWithDays("-7 days")
//                "MONTHLY" -> statisticsDao.getRecordsSuppliersWithDays("-30 days")
//                "QUARTERLY" -> statisticsDao.getRecordsSuppliersWithDays("-90 days")
//                "BIANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-180 days")
//                "ANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-360 days")
//                "HISTORICAL" -> statisticsDao.getAllSuppliersRecords()
//                else -> statisticsDao.getRecordsSuppliersWithDaysFrame(startTime, endTime)
//            }
//            RepoResult.Success(data)
//        }catch (e:Exception){
//            RepoResult.Error(e)
//        }
//    }
}
