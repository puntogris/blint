package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.model.RoomUser
import com.puntogris.blint.utils.Constants.APP_VERSION
import com.puntogris.blint.utils.Constants.BACKUP_PATH
import com.puntogris.blint.utils.Constants.BUSINESS_COLLECTION
import com.puntogris.blint.utils.Constants.CATEGORIES_COLLECTION
import com.puntogris.blint.utils.Constants.CLIENTS_COLLECTION
import com.puntogris.blint.utils.Constants.EVENTS_COLLECTION
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.NOTIFICATIONS_SUB_COLLECTION
import com.puntogris.blint.utils.Constants.ORDERS_COLLECTION
import com.puntogris.blint.utils.Constants.OWNER_FIELD
import com.puntogris.blint.utils.Constants.PRODUCTS_COLLECTION
import com.puntogris.blint.utils.Constants.RECORDS_COLLECTION
import com.puntogris.blint.utils.Constants.SUPPLIERS_COLLECTION
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD
import com.puntogris.blint.utils.Constants.TYPE_FIELD
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.Constants.USERS_PATH
import com.puntogris.blint.utils.Constants.WAS_READ_FIELD
import com.puntogris.blint.utils.Util.getPathToUserReceivedNotifications
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class FirestoreQueries @Inject constructor(){

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private fun currentUserId() = auth.currentUser?.uid.toString()
    private val storage =  Firebase.storage.reference

    fun getUserBackupStorageQuery() = storage.child("$USERS_PATH/${currentUserId()}/${BACKUP_PATH}_$APP_VERSION")

    fun test() = storage.child("users/test.webp").metadata

    fun getUserBusinessProductImagesQuery(user: RoomUser, imageName:String) =
        storage.child("users/${user.currentBusinessOwner}/business/${user.currentBusinessId}/products_images/$imageName")

    fun getUserLocalBusinessQuery() =
        firestore
            .collectionGroup(BUSINESS_COLLECTION)
            .whereEqualTo(OWNER_FIELD, currentUserId())
            .whereEqualTo(TYPE_FIELD, LOCAL)

    fun getUserNotificationsQuery() =
        firestore
            .collection(getPathToUserReceivedNotifications(currentUserId()))
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(15)
            .get()

    fun getUserNotificationsAfterLastQuery(lastVisible: DocumentSnapshot) =
        firestore
            .collection(getPathToUserReceivedNotifications(currentUserId()))
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(15)
            .startAfter(lastVisible)
            .get()

    fun getAllUnreadNotificationsQuery() =
        firestore
            .collection(USERS_COLLECTION)
            .document(currentUserId())
            .collection(NOTIFICATIONS_SUB_COLLECTION)
            .whereEqualTo(WAS_READ_FIELD, false)
            .limit(11)

    fun updateNotificationsReadStateQuery() =
        firestore.collection(USERS_COLLECTION)
            .document(currentUserId())
            .collection(NOTIFICATIONS_SUB_COLLECTION)

    fun deleteNotificationQuery() =
        firestore.collection(getPathToUserReceivedNotifications(currentUserId()))

    fun getBusinessCollectionQuery(user: RoomUser) = firestore
        .collection(USERS_COLLECTION)
        .document(user.currentBusinessOwner)
        .collection(BUSINESS_COLLECTION)
        .document(user.currentBusinessId)

    fun getBusinessCountersQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection("statistics").document("counters")

    fun getEventsCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(EVENTS_COLLECTION)

    fun getProductsCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(PRODUCTS_COLLECTION)

    fun getRecordsCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(RECORDS_COLLECTION)

    fun getClientsCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(CLIENTS_COLLECTION)

    fun getSuppliersCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(SUPPLIERS_COLLECTION)

    fun getCategoriesCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(CATEGORIES_COLLECTION)

    fun getOrdersCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection(ORDERS_COLLECTION)

    fun getStatisticsCollectionQuery(user: RoomUser) = getBusinessCollectionQuery(user).collection("statistics")


    fun getRecordsWithTraderIdQuery(user: RoomUser, traderId: String) =
        getRecordsCollectionQuery(user).whereEqualTo("traderId", traderId)

}