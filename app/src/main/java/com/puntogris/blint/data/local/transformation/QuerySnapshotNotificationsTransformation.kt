package com.puntogris.blint.data.local.transformation

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.puntogris.blint.model.notifications.*
import com.puntogris.blint.utils.Constants.ADVERTISEMENT_NOTIFICATION
import com.puntogris.blint.utils.Constants.ADVERTISEMENT_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_SENT_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_SENT_NOTIFICATION_SERVER
import com.puntogris.blint.utils.NotificationType

object QuerySnapshotNotificationsTransformation {
    fun transform(querySnapshot: QuerySnapshot): List<NotificationType>{
        return querySnapshot.map { doc->
            when(notificationClientType(doc.get("type").toString())) {
                ADVERTISEMENT_NOTIFICATION ->
                    NotificationType.Advertisement(doc.toObject(AdvertisementNotification::class.java))
                EMPLOYMENT_REQUEST_SENT_NOTIFICATION ->
                    NotificationType.SentRequest(doc.toObject(EmploymentRequestSentNotif::class.java))
                EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION ->
                    NotificationType.ReceivedRequest(doc.toObject(EmploymentRequestReceivedNotif::class.java))
                EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION ->
                    NotificationType.OwnerRequestResponse(doc.toObject(EmploymentResponseOwnerNotif::class.java))
                else ->
                    NotificationType.EmployeeRequestResponse(doc.toObject(EmploymentResponseEmployeeNotif::class.java))
            }
        }
    }
    //Changes de code for the notification from server side to client side to use in viewType in the adapter
    private fun notificationClientType(serverType: String): Int{
        return when(serverType){
            ADVERTISEMENT_NOTIFICATION_SERVER -> ADVERTISEMENT_NOTIFICATION
            EMPLOYMENT_REQUEST_SENT_NOTIFICATION_SERVER -> EMPLOYMENT_REQUEST_SENT_NOTIFICATION
            EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION_SERVER -> EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION
            EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION_SERVER -> EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION
            else -> EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION
        }
    }
}