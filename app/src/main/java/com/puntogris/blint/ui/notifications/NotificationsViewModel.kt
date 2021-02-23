package com.puntogris.blint.ui.notifications

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.data.remote.NotificationsRepository
import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class NotificationsViewModel @ViewModelInject constructor(private val notificationsRepository: NotificationsRepository): ViewModel() {

    val notificationsFetchResult = MutableStateFlow<NotificationsState>(NotificationsState.Working.LoadFirstBatch)

    suspend fun getFirstBatchNotifications(){
        viewModelScope.launch {
            notificationsFetchResult.emitAll(notificationsRepository.getFirstBatchNotifications())
        }
    }

    suspend fun getMoreNotifications(){
        viewModelScope.launch {
            notificationsFetchResult.emitAll(notificationsRepository.getMoreNotifications())
        }
    }

    fun deleteNotification(notificationID:String){
        notificationsRepository.deleteNotification(notificationID)
    }

    fun changeReadStatusNotification(notificationID: String){
        notificationsRepository.updateNotificationReadState(notificationID)
    }

}