package com.puntogris.blint.data.repo

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.repo.imp.IStatisticRepository
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class StatisticRepository @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val usersDao: UsersDao,
): IStatisticRepository {

    val firestore = Firebase.firestore
    suspend fun currentUser() = usersDao.getUser()


    override suspend fun getAllClients(): RepoResult<List<Client>> {
        val user = currentUser()
        return try {
            val data =
                if (user.currentBusinessIsOnline()){
                firestore.collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("clients")
                    .limit(500)
                    .get().await().toObjects(Client::class.java)
            }else{
                statisticsDao.getAllClients()
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

    override suspend fun getAllProducts(): RepoResult<List<Product>> {
        val user = currentUser()
        return try {
            val data = if (user.currentBusinessIsOnline()){
                firestore.collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("products")
                    .limit(500)
                    .get().await().toObjects(Product::class.java)
            }else{
                statisticsDao.getAllProducts()
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

    override suspend fun getAllSuppliers(): RepoResult<List<Supplier>> {
        val user = currentUser()
        return try {
            val data = if (user.currentBusinessIsOnline()){
                firestore.collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("suppliers")
                    .limit(500)
                    .get().await().toObjects(Supplier::class.java)
            }else{
                statisticsDao.getAllSuppliers()
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

    override suspend fun getBusinessCounters(): RepoResult<BusinessCounters> {
        val user = currentUser()
        return try {
            val data = if (user.currentBusinessIsOnline()) {

                firestore.collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("statistics")
                    .document("counters")
                    .get().await().toObject(BusinessCounters::class.java) ?: BusinessCounters()
            }else{
                statisticsDao.getStatistics()
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

}