package com.puntogris.blint.data.repo.irepo

import androidx.lifecycle.LiveData
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.RoomUser
import com.puntogris.blint.utils.EventsDashboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard
    fun getAllUnreadNotifications(): StateFlow<Int>
    suspend fun getBusinessCounterFlow(): Flow<BusinessCounters>
    fun checkIfUserIsLogged():Boolean
    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner:String)
    fun getUserLiveDataRoom(): LiveData<RoomUser>
    suspend fun getBusinessListRoom(): List<Employee>
}