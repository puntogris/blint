package com.puntogris.blint.ui.employee

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.employees.EmployeesRepository
import com.puntogris.blint.model.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeesRepository: EmployeesRepository
): ViewModel() {

    suspend fun hasUserOwnerPermissions(businessId: String) = employeesRepository.getEmployeeWithBusinessId(businessId)

    suspend fun deleteEmployeeFromBusiness(employee: Employee) =
        employeesRepository.deleteEmployeeFromBusiness(employee)
}