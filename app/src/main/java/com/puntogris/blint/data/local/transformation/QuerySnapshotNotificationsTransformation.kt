package com.puntogris.blint.data.local.transformation

import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.notifications.EmploymentResponseOwnerNotif
import com.puntogris.blint.model.notifications.AdvertisementNotification
import com.puntogris.blint.model.notifications.EmploymentRequestReceivedNotif
import com.puntogris.blint.model.notifications.EmploymentResponseEmployeeNotif
import com.puntogris.blint.utils.Constants.ACCEPTED_EMPLOYEE_REQUEST_NOTIFICATION
import com.puntogris.blint.utils.Constants.ACCEPTED_EMPLOYEE_REQUEST_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.ADVERTISEMENT_NOTIFICATION
import com.puntogris.blint.utils.Constants.ADVERTISEMENT_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.DENIED_EMPLOYEE_REQUEST_NOTIFICATION
import com.puntogris.blint.utils.Constants.DENIED_EMPLOYEE_REQUEST_NOTIFICATION_SERVER
import com.puntogris.blint.utils.Constants.NEW_EMPLOYEE_REQUEST_NOTIFICATION
import com.puntogris.blint.utils.NotificationType

object QuerySnapshotNotificationsTransformation {
    fun transform(querySnapshot: QuerySnapshot): List<NotificationType>{
        return querySnapshot.map { doc->
            when(notificationClientType(doc.get("type").toString())) {
                ADVERTISEMENT_NOTIFICATION -> NotificationType.Advertisement(doc.toObject(
                    AdvertisementNotification::class.java))
                ACCEPTED_EMPLOYEE_REQUEST_NOTIFICATION -> NotificationType.OwnerRequestResponse(doc.toObject(
                    EmploymentResponseOwnerNotif::class.java))
                DENIED_EMPLOYEE_REQUEST_NOTIFICATION -> NotificationType.ReceivedRequestNotification(doc.toObject(
                    EmploymentRequestReceivedNotif::class.java))
                else -> NotificationType.EmployeeRequestResponse(doc.toObject(
                    EmploymentResponseEmployeeNotif::class.java))
            }
        }
    }
    //Changes de code for the notification from server side to client side to use in viewType in the adapter
    private fun notificationClientType(serverType: String): Int{
        return when(serverType){
            ADVERTISEMENT_NOTIFICATION_SERVER -> ADVERTISEMENT_NOTIFICATION
            ACCEPTED_EMPLOYEE_REQUEST_NOTIFICATION_SERVER -> ACCEPTED_EMPLOYEE_REQUEST_NOTIFICATION
            DENIED_EMPLOYEE_REQUEST_NOTIFICATION_SERVER -> DENIED_EMPLOYEE_REQUEST_NOTIFICATION
            else -> NEW_EMPLOYEE_REQUEST_NOTIFICATION
        }
    }
}