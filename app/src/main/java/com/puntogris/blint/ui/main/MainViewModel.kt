package com.puntogris.blint.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.RoomUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao,
    private val productsDao: ProductsDao,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao
):ViewModel() {

    private val _businesses = MutableStateFlow(listOf(Business()))
    val businesses: StateFlow<List<Business>> = _businesses

    fun getBusiness(){
        viewModelScope.launch {
            _businesses.emit(businessDao.getBusinesses())
        }
    }

    suspend fun getCurrentBusiness() = usersDao.getUser()

    suspend fun getProductsCount() = productsDao.getCount()

    suspend fun getClientsCount() = clientsDao.getCount()

    suspend fun getSuppliersCount() = suppliersDao.getCount()

    fun isUserLoggedIn() = userRepository.checkIfUserIsLogged()

    suspend fun updateCurrentBusiness(position:Int){
        val business = _businesses.value[position]
        usersDao.insert(RoomUser(
            id = "1",
            currentBusinessId = business.businessId,
            currentBusinessType = business.type,
            currentBusinessName = business.name,
            currentUid = userRepository.getCurrentUID()
        ))
    }
}