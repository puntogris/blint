package com.puntogris.blint.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.business.BusinessDao
import com.puntogris.blint.model.Business

class MainViewModel @ViewModelInject constructor(
        private val businessDao: BusinessDao
):ViewModel() {

    suspend fun getBusiness() = businessDao.getBusinesses()

    suspend fun getBusinessCount() = businessDao.getCount()

    suspend fun registerNewBusiness(name: String){
        businessDao.insert(Business(name = name))
    }
}