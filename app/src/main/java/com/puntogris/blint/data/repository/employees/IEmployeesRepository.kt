package com.puntogris.blint.data.repository.employees

import com.puntogris.blint.model.Employee
import com.puntogris.blint.utils.JoinBusiness
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.UserBusiness
import kotlinx.coroutines.flow.StateFlow

interface IEmployeesRepository {
    suspend fun getEmployeeListRoom(): List<Employee>
    suspend fun deleteEmployeeFromBusiness(employee: Employee): SimpleResult
    suspend fun createEmployeeWithCode(code:String): JoinBusiness
    suspend fun getEmployeeWithBusinessId(businessId: String): Employee
    fun getBusinessEmployees(businessId:String): StateFlow<UserBusiness>

}