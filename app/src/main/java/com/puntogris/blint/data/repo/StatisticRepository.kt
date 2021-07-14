package com.puntogris.blint.data.repo

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.repo.irepo.IStatisticRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

class StatisticRepository @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val usersDao: UsersDao,
    private val firestoreQueries: FirestoreQueries
): IStatisticRepository {

    val firestore = Firebase.firestore
    suspend fun currentUser() = usersDao.getUser()

    override suspend fun getAllClients(): RepoResult<List<Client>> = withContext(Dispatchers.IO){
        val user = currentUser()
        try {
            val data = if (user.currentBusinessIsOnline()){
                firestoreQueries.getClientsCollectionQuery(user)
                    .limit(500)
                    .get().await().toObjects(Client::class.java)
            }else{
                statisticsDao.getAllClients()
            }
            RepoResult.Success(data)
        }catch (e:Exception){ RepoResult.Error(e) }
    }

    override suspend fun getAllProducts(): RepoResult<List<Product>> = withContext(Dispatchers.IO){
        val user = currentUser()
        try {
            val data = if (user.currentBusinessIsOnline()){
                firestoreQueries.getProductsCollectionQuery(user)
                    .limit(500)
                    .get().await().toObjects(Product::class.java)
            }else{
                statisticsDao.getAllProducts()
            }
            RepoResult.Success(data)
        }catch (e:Exception){ RepoResult.Error(e) }
    }

    override suspend fun getAllSuppliers(): RepoResult<List<Supplier>> = withContext(Dispatchers.IO){
        val user = currentUser()
        try {
            val data = if (user.currentBusinessIsOnline()){
                firestoreQueries.getSuppliersCollectionQuery(user)
                    .limit(500)
                    .get().await().toObjects(Supplier::class.java)
            }else{
                statisticsDao.getAllSuppliers()
            }
            RepoResult.Success(data)
        }catch (e:Exception){ RepoResult.Error(e) }
    }

    override suspend fun getBusinessCounters(): RepoResult<BusinessCounters> = withContext(Dispatchers.IO){
        val user = currentUser()
        try {
            val data = if (user.currentBusinessIsOnline()) {
                firestoreQueries.getBusinessCountersQuery(user)
                    .get().await().toObject(BusinessCounters::class.java) ?: BusinessCounters()
            }else{
                statisticsDao.getStatistics()
            }
            RepoResult.Success(data)
        }catch (e:Exception){ RepoResult.Error(e) }
    }

    override suspend fun getProductsReports(timeCode: String): RepoResult<List<ProductRecordExcel>> = withContext(Dispatchers.IO){
        val user = currentUser()
        try {
            val data = if (user.currentBusinessIsOnline()) {
                val products = firestoreQueries.getProductsCollectionQuery(user)
                    .limit(500)
                    .get().await().toObjects(Product::class.java)

                products.removeIf { it.totalInStock == 0 && it.totalOutStock == 0 }

                if (timeCode == "HISTORICAL"){
                    products.map { ProductRecordExcel(
                        it.name,
                        it.totalInStock,
                        it.totalOutStock
                    ) }
                }else{
                    val excelList = mutableListOf<ProductRecordExcel>()

                    val calendar = Calendar.getInstance()
                    calendar.time = Date()
                    calendar.add(
                        Calendar.DAY_OF_YEAR,
                        when(timeCode) {
                            "WEEKLY" -> -7
                            "MONTHLY" -> -30
                            "QUARTERLY" -> -90
                            "BIANNUAL" -> -180
                            else -> -361
                        }
                    )
                    products.forEach {
                        val data = firestoreQueries.getRecordsCollectionQuery(user)
                            .whereEqualTo("productId", it.productId)
                            .whereGreaterThanOrEqualTo("timestamp", Timestamp(calendar.time.time / 1000, 0))
                            .orderBy("timestamp", Query.Direction.ASCENDING)
                            .limit(1)
                            .get().await()

                        if (data.documents.isNotEmpty()){
                            data.documents.first().toObject(Record::class.java)?.let { record->
                                ProductRecordExcel(
                                    name = it.name,
                                    totalInStock = if (record.type == "IN") it.totalInStock - (record.totalInStock - record.amount).absoluteValue
                                    else it.totalInStock - record.totalInStock,
                                    totalOutStock = if (record.type == "OUT") it.totalOutStock - (record.totalOutStock - record.amount).absoluteValue
                                    else it.totalOutStock - record.totalOutStock
                                )
                            }
                        }
                    }
                    excelList
                }
            }else{
                when(timeCode){
                    "WEEKLY" -> statisticsDao.getProductRecordDaysExcelList("-7 days")
                    "MONTHLY" -> statisticsDao.getProductRecordDaysExcelList("-30 days")
                    "QUARTERLY" -> statisticsDao.getProductRecordDaysExcelList("-90 days")
                    "BIANNUAL" -> statisticsDao.getProductRecordDaysExcelList("-180 days")
                    "ANNUAL" -> statisticsDao.getProductRecordDaysExcelList("-360 days")
                    else -> statisticsDao.getProductRecordExcelList()
                }
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }
//
//    override suspend fun getClientsReports(timeCode: String, startTime:Long, endTime:Long): RepoResult<List<ClientRecordExcel>> {
//        val user = currentUser()
//        return try {
//            val data = if (user.currentBusinessIsOnline()) {
//                val clients = firestoreQueries.getBusinessCollectionQuery(user)
//                    .collection("statistics")
//                    .document("clients_records")
//                    .get().await().toObject(ClientsRecords::class.java)
//
//                val excel = mutableListOf<ClientRecordExcel>()
//
//                clients?.clients?.forEach { client ->
//                    client.productsIds.forEach { productId ->
//                        val initQuery = firestoreQueries.getRecordsCollectionQuery(user)
//                            .whereEqualTo("traderId", client.clientId)
//                            .whereEqualTo("productId", productId)
//                            .limit(1)
//
//                        val query = when(timeCode) {
//                            "WEEKLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "MONTHLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "QUARTERLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "BIANNUAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "ANNUAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "HISTORICAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            else -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                        }
//
//                        val data = query.get().await()
//
//                        val clientExcel = ClientRecordExcel()
//                        excel.add(clientExcel)
//                    }
//                }
//                excel
//            }else{
//                when(timeCode){
//                    "WEEKLY" -> statisticsDao.getRecordsClientsWithDays("-7 days")
//                    "MONTHLY" -> statisticsDao.getRecordsClientsWithDays("-30 days")
//                    "QUARTERLY" -> statisticsDao.getRecordsClientsWithDays("-90 days")
//                    "BIANNUAL" -> statisticsDao.getRecordsClientsWithDays("-180 days")
//                    "ANNUAL" -> statisticsDao.getRecordsClientsWithDays("-360 days")
//                    "HISTORICAL" -> statisticsDao.getAllClientsRecords()
//                    else -> statisticsDao.getRecordsClientsWithDaysFrame(startTime, endTime)
//                }
//            }
//            RepoResult.Success(data)
//        }catch (e:Exception){
//            RepoResult.Error(e)
//        }
//    }
//
//    override suspend fun getSuppliersReports(timeCode: String, startTime:Long, endTime:Long): RepoResult<List<SupplierRecordExcel>> {
//        val user = currentUser()
//        return try {
//            val data = if (user.currentBusinessIsOnline()) {
//                val clients = firestoreQueries.getBusinessCollectionQuery(user)
//                    .collection("statistics")
//                    .document("suppliers_records")
//                    .get().await().toObject(SuppliersRecords::class.java)
//
//                val excel = mutableListOf<SupplierRecordExcel>()
//
//                clients?.suppliers?.forEach { client ->
//                    client.productsIds.forEach { productId ->
//                        val initQuery = firestoreQueries.getRecordsCollectionQuery(user)
//                            .whereEqualTo("traderId", client.supplierId)
//                            .whereEqualTo("productId", productId)
//                            .limit(1)
//
//                        val query = when(timeCode) {
//                            "WEEKLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "MONTHLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "QUARTERLY" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "BIANNUAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "ANNUAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            "HISTORICAL" -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                            else -> initQuery.whereLessThanOrEqualTo("timestamp", "")
//                        }
//
//                        val data = query.get().await()
//
//                        val clientExcel = SupplierRecordExcel("nombreProve", listOf(ProductRecord("nomprePrdu", 0)) )
//                        excel.add(clientExcel)
//                    }
//                }
//                excel
//            }else{
//                when(timeCode){
//                    "WEEKLY" -> statisticsDao.getRecordsSuppliersWithDays("-7 days")
//                    "MONTHLY" -> statisticsDao.getRecordsSuppliersWithDays("-30 days")
//                    "QUARTERLY" -> statisticsDao.getRecordsSuppliersWithDays("-90 days")
//                    "BIANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-180 days")
//                    "ANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-360 days")
//                    "HISTORICAL" -> statisticsDao.getAllSuppliersRecords()
//                    else -> statisticsDao.getRecordsSuppliersWithDaysFrame(startTime, endTime)
//                }
//            }
//            RepoResult.Success(data)
//        }catch (e:Exception){
//            RepoResult.Error(e)
//        }
//    }
}