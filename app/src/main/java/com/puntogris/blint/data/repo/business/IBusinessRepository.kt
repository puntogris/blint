package com.puntogris.blint.data.repo.business

import com.puntogris.blint.model.Employee
import com.puntogris.blint.utils.DeleteBusiness
import com.puntogris.blint.utils.SimpleResult

interface IBusinessRepository {
    suspend fun registerLocalBusiness(businessName: String):SimpleResult
    suspend fun deleteBusinessDatabase(businessId: String): DeleteBusiness
    suspend fun deleteEmployeeFromBusiness(employee: Employee):SimpleResult
}