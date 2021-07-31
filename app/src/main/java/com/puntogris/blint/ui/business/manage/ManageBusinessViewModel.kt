package com.puntogris.blint.ui.business.manage

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.employees.EmployeesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageBusinessViewModel @Inject constructor(
    private val employeesRepository: EmployeesRepository
): ViewModel() {

    suspend fun getBusinessList() = employeesRepository.getEmployeeListRoom()

}