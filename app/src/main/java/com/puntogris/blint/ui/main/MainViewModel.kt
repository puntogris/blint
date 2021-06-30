package com.puntogris.blint.ui.main

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.MainRepository
import com.puntogris.blint.data.remote.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val usersDao: UsersDao,
    private val employeeDao: EmployeeDao,
    productsDao: ProductsDao,
    suppliersDao: SuppliersDao,
    clientsDao: ClientsDao,
    private val mainRepository: MainRepository
):ViewModel() {

    val productsCount = productsDao.getCount()
    val clientsCount = clientsDao.getCount()
    val suppliersCount = suppliersDao.getCount()

    fun isUserLoggedIn() = userRepository.checkIfUserIsLogged()

    fun getUnreadNotificationsCount() = mainRepository.getAllUnreadNotifications()

    suspend fun getBusinessLastEvents() = mainRepository.getBusinessLastEventsDatabase()

    fun getEmployeeBusiness() = employeeDao.getEmployeesListLiveData()

    fun getCurrentBusiness() = usersDao.getUserLiveData()

    suspend fun getBusinessList() = employeeDao.getEmployeesList()

    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner: String) = usersDao.updateCurrentBusiness(id, name, type, owner)
}