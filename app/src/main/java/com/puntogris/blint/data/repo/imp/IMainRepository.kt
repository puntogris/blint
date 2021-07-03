package com.puntogris.blint.data.repo.imp

import com.puntogris.blint.utils.EventsDashboard
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard
    fun getAllUnreadNotifications(): StateFlow<Int>
}