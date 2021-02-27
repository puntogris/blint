package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.utils.Constants.NOTIFICATIONS_SUB_COLLECTION
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.Constants.WAS_READ_FIELD
import com.puntogris.blint.utils.Util.getPathToUserReceivedNotifications
import javax.inject.Inject

class FirestoreQueries @Inject constructor(){

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId = auth.currentUser?.uid.toString()

    fun getUserNotificationsQuery() =
        firestore
            .collection(getPathToUserReceivedNotifications(currentUserId))
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(15)
            .get()


    fun getUserNotificationsAfterLastQuery(lastVisible: DocumentSnapshot) =
        firestore
            .collection(getPathToUserReceivedNotifications(currentUserId))
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(15)
            .startAfter(lastVisible)
            .get()

    fun getAllUnreadNotificationsQuery() =
        firestore
            .collection(USERS_COLLECTION)
            .document(currentUserId)
            .collection(NOTIFICATIONS_SUB_COLLECTION)
            .whereEqualTo(WAS_READ_FIELD, false)
            .limit(11)

    fun updateNotificationsReadStateQuery() =
        firestore.collection(USERS_COLLECTION)
            .document(currentUserId)
            .collection(NOTIFICATIONS_SUB_COLLECTION)

    fun deleteNotificationQuery() =
        firestore.collection(getPathToUserReceivedNotifications(currentUserId))
}