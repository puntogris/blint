package com.puntogris.blint.data.repository.business

import com.puntogris.blint.model.JoinCode
import com.puntogris.blint.utils.DeleteBusiness
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult

interface IBusinessRepository {
    suspend fun registerLocalBusiness(businessName: String):SimpleResult
    suspend fun deleteBusinessDatabase(businessId: String): DeleteBusiness
}