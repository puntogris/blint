package com.puntogris.blint.ui.business

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.EmployeeRequest

class BusinessViewModel @ViewModelInject constructor(
    private val employeeDao: EmployeeDao,
    private val userRepository: UserRepository): ViewModel() {

    suspend fun getBusiness() = employeeDao.getEmployeesList()

    fun getBusinessEmployees(businessId:String) = userRepository.getBusinessEmployees(businessId)

    suspend fun hasUserOwnerPermissions(employeeId:String) :Boolean{
        return employeeDao.getBusinessUserRole(employeeId) == "ADMINISTRATOR"
    }

    fun sendRequest(request: EmployeeRequest) = userRepository.sendEmployeeRequest(request)

}