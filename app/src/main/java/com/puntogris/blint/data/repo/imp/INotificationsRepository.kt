package com.puntogris.blint.data.repo.imp

import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.StateFlow

interface INotificationsRepository {
    fun getFirstBatchNotifications(): StateFlow<NotificationsState>
    fun getMoreNotifications(): StateFlow<NotificationsState>
    fun deleteNotification(notificationId:String)
    fun updateNotificationReadState(notificationId: String)
    }