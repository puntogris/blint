package com.puntogris.blint.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.data.repo.notifications.NotificationsRepository
import com.puntogris.blint.utils.NotificationsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val notificationsRepository: NotificationsRepository): ViewModel() {

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

    fun deleteNotification(notificationId:String){
        notificationsRepository.deleteNotification(notificationId)
    }

    fun changeReadStatusNotification(notificationId: String){
        notificationsRepository.updateNotificationReadState(notificationId)
    }

}