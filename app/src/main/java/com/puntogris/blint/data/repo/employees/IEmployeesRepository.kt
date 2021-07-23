package com.puntogris.blint.data.repo.employees

import com.puntogris.blint.model.Employee
import com.puntogris.blint.utils.JoinBusiness
import com.puntogris.blint.utils.SimpleResult

interface IEmployeesRepository {
    suspend fun getEmployeeListRoom(): List<Employee>
    suspend fun deleteEmployeeFromBusiness(employee: Employee): SimpleResult
    suspend fun createEmployeeWithCode(code:String): JoinBusiness

}