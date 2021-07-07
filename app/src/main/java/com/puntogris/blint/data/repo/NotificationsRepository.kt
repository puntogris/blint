package com.puntogris.blint.data.repo

import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.repo.imp.INotificationsRepository
import com.puntogris.blint.model.Notification
import com.puntogris.blint.utils.Constants.WAS_READ_FIELD
import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val firestoreQueries: FirestoreQueries):
    INotificationsRepository {

    private lateinit var lastVisible: DocumentSnapshot
    private val list = mutableListOf<Notification>()

    override fun getFirstBatchNotifications(): StateFlow<NotificationsState> =
        MutableStateFlow<NotificationsState>(NotificationsState.Idle).also {
            firestoreQueries.getUserNotificationsQuery()
                .addOnSuccessListener { snap ->
                    if (snap.documents.isNotEmpty()){
                        lastVisible = snap.documents.last()
                        list.addAll(snap.toObjects(Notification::class.java))
                        it.value = NotificationsState.Success(list)
                    }else it.value = NotificationsState.CollectionEmpty
                }.addOnFailureListener { e ->
                    it.value = NotificationsState.Error(e)
                }
        }

    override fun getMoreNotifications(): StateFlow<NotificationsState> =
        MutableStateFlow<NotificationsState>(NotificationsState.Idle)
            .also {
                firestoreQueries.getUserNotificationsAfterLastQuery(lastVisible)
                    .addOnSuccessListener { snap ->
                        if (snap.documents.isNotEmpty()) {
                            list.addAll(snap.toObjects(Notification::class.java))
                            lastVisible = snap.documents.last()
                            if (snap.documents.size < 15) it.value = NotificationsState.CollectionEmpty
                            it.value = NotificationsState.Success(list)
                        } else it.value = NotificationsState.CollectionEmpty
                    }.addOnFailureListener { e -> it.value = NotificationsState.Error(e) }
            }

    override fun deleteNotification(notificationId: String) {
        if (notificationId.isNotEmpty())
            firestoreQueries.deleteNotificationQuery().document(notificationId).delete()
    }

    override fun updateNotificationReadState(notificationId: String) {
        firestoreQueries.updateNotificationsReadStateQuery()
            .document(notificationId)
            .update(WAS_READ_FIELD, true)
    }
}