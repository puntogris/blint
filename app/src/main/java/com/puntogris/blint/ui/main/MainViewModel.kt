package com.puntogris.blint.ui.main

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.NotificationsRepository
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.RoomUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val usersDao: UsersDao,
    private val eventsDao: EventsDao,
    private val notificationsRepository: NotificationsRepository,
    private val employeeDao: EmployeeDao,
    productsDao: ProductsDao,
    suppliersDao: SuppliersDao,
    clientsDao: ClientsDao
):ViewModel() {

    val productsCount = productsDao.getCount()
    val clientsCount = clientsDao.getCount()
    val suppliersCount = suppliersDao.getCount()

    private val _businesses = MutableStateFlow(listOf(Employee()))

    fun isUserLoggedIn() = userRepository.checkIfUserIsLogged()

    suspend fun getBusinessCount() = employeeDao.getCount()

    fun getUnreadNotificationsCount() = notificationsRepository.getAllUnreadNotifications()

    fun getLastEvents() = eventsDao.getLastThreeEvents()

    fun getEmployeeBusiness() = employeeDao.getEmployeesListLiveData()

    fun getCurrentBusiness() = usersDao.getUserLiveData()

    suspend fun getBusinessList() = employeeDao.getEmployeesList()

    suspend fun updateCurrentBusiness(id:String, name:String, type:String) = usersDao.updateCurrentBusiness(id, name, type)
}