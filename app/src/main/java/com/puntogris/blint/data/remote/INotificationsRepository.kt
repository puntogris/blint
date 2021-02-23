package com.puntogris.blint.data.remote

import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.StateFlow

interface INotificationsRepository {
    fun getFirstBatchNotifications(): StateFlow<NotificationsState>
    fun getMoreNotifications(): StateFlow<NotificationsState>
    fun deleteNotification(notificationId:String)
    fun getAllUnreadNotifications(): StateFlow<Int>
    fun updateNotificationReadState(notificationId: String)
    }