package com.puntogris.blint.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.data.local.businesses.BusinessDao
import com.puntogris.blint.data.local.clients.ClientsDao
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.suppliers.SuppliersDao
import com.puntogris.blint.model.Business

class MainViewModel @ViewModelInject constructor(
        private val businessDao: BusinessDao,
        private val productsDao: ProductsDao,
        private val clientsDao: ClientsDao,
        private val suppliersDao: SuppliersDao
):ViewModel() {

    suspend fun getBusiness() = businessDao.getBusinesses()

    suspend fun getBusinessCount() = businessDao.getCount()

    suspend fun getProductsCount() = productsDao.getCount()

    suspend fun getClientsCount() = clientsDao.getCount()

    suspend fun getSuppliersCount() = suppliersDao.getCount()


    //create product listener db
    //then update ui

    // client listener db

    //supplier listener from db


    suspend fun registerNewBusiness(name: String){
        businessDao.insert(Business(name = name))
    }
}