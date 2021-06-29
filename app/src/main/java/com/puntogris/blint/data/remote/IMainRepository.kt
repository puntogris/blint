package com.puntogris.blint.data.remote

import com.puntogris.blint.utils.EventsDashboard

interface IMainRepository {
    suspend fun getLastBusinessEvents(): EventsDashboard
}