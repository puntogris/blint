package com.puntogris.blint.ui.business

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.business.BusinessRepository
import com.puntogris.blint.data.repository.employees.EmployeesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val employeesRepository: EmployeesRepository
): ViewModel() {

    fun getBusinessEmployees(businessId:String) = employeesRepository.getBusinessEmployees(businessId)

    suspend fun getBusinessEmployee(businessId: String) = employeesRepository.getEmployeeWithBusinessId(businessId)

    suspend fun createEmployee(code:String) = employeesRepository.createEmployeeWithCode(code)

    suspend fun deleteBusiness(businessId: String) =
        businessRepository.deleteBusinessDatabase(businessId)

}