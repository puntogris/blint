package com.puntogris.blint.data.repo.employees

import com.puntogris.blint.data.local.dao.EmployeeDao
import javax.inject.Inject

class EmployeesRepository @Inject constructor(
    private val employeeDao: EmployeeDao
):IEmployeesRepository {

    override suspend fun getEmployeeListRoom() = employeeDao.getEmployeesList()

}