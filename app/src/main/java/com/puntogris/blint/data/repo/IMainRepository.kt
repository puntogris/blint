package com.puntogris.blint.data.repo

import com.puntogris.blint.utils.EventsDashboard
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard
    fun getAllUnreadNotifications(): StateFlow<Int>
}