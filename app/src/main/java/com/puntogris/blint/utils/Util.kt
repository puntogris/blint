package com.puntogris.blint.utils

import com.google.firebase.Timestamp
import com.puntogris.blint.utils.Constants.NOTIFICATIONS_SUB_COLLECTION
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import java.io.*
import java.nio.channels.FileChannel

object Util {

    fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            try {
                fromChannel?.close()
            } finally {
                toChannel?.close()
            }
        }
    }

    fun isTimeStampExpired(timestamp: Timestamp):Boolean{
        val lastCodeTime = timestamp.seconds
        val timeNow = Timestamp.now().seconds
        val timeDifference = timeNow - lastCodeTime
        return timeDifference >= 600 //10 minutes
    }

    fun getPathToUserReceivedNotifications(uid:String) =
        "${USERS_COLLECTION}/$uid/${NOTIFICATIONS_SUB_COLLECTION}"

}