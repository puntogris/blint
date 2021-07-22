package com.puntogris.blint.data.repo.employees

import com.puntogris.blint.model.Employee

interface IEmployeesRepository {
    suspend fun getEmployeeListRoom(): List<Employee>
}