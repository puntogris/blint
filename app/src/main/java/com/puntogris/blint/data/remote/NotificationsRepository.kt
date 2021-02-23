package com.puntogris.blint.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.data.local.transformation.QuerySnapshotNotificationsTransformation
import com.puntogris.blint.utils.Constants.WAS_READ_FIELD
import com.puntogris.blint.utils.NotificationType
import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val firestoreQueries: FirestoreQueries): INotificationsRepository {

    private lateinit var lastVisible: DocumentSnapshot
    private val list = mutableListOf<NotificationType>()

    override fun getFirstBatchNotifications(): StateFlow<NotificationsState> =
        MutableStateFlow<NotificationsState>(NotificationsState.Idle).also {
            firestoreQueries.getUserNotificationsQuery()
                .addOnSuccessListener { snap ->
                    if (snap.documents.isNotEmpty()){
                        lastVisible = snap.documents.last()
                        list.addAll(QuerySnapshotNotificationsTransformation.transform(snap))
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
                            list.addAll(QuerySnapshotNotificationsTransformation.transform(snap))
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

    override fun getAllUnreadNotifications(): StateFlow<Int> =
        MutableStateFlow(0).also { stateFlow ->
            firestoreQueries.getAllUnreadNotificationsQuery()
                .addSnapshotListener { snap, _ ->
                    snap?.documents?.let { docs ->
                        stateFlow.value = docs.size
                    }
                }
        }


    override fun updateNotificationReadState(notificationId: String) {
        firestoreQueries.updateNotificationsReadStateQuery()
            .document(notificationId)
            .update(WAS_READ_FIELD, true)
    }
}