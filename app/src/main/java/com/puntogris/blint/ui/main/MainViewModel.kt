package com.puntogris.blint.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.BusinessDao
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.remote.UserRepository

class MainViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao,
    private val productsDao: ProductsDao,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao
):ViewModel() {

    suspend fun getBusiness() = businessDao.getBusinesses()

    suspend fun userHasBusinessRegistered() = businessDao.getCount() > 1

    suspend fun getProductsCount() = productsDao.getCount()

    suspend fun getClientsCount() = clientsDao.getCount()

    suspend fun getSuppliersCount() = suppliersDao.getCount()

    fun isUserLoggedIn() = userRepository.checkIfUserIsLogged()

    suspend fun registerNewBusiness(name: String){
        userRepository.registerNewBusiness(name)
    }
}