package com.puntogris.blint.utils

import android.content.Context
import android.content.res.Configuration
import com.google.firebase.Timestamp
import com.puntogris.blint.utils.Constants.NOTIFICATIONS_SUB_COLLECTION
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import java.io.*
import java.nio.channels.FileChannel
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.seconds

object Util {

  fun getGaussianScale(
    childCenterX: Int,
    minScaleOffest: Float,
    scaleFactor: Float,
    spreadFactor: Double,
    left: Int,
    right: Int
  ): Float {
    val recyclerCenterX = (left + right) / 2
    return (Math.pow(
        Math.E,
        -Math.pow(childCenterX - recyclerCenterX.toDouble(), 2.toDouble()) / (2 * Math.pow(
            spreadFactor,
            2.toDouble()
        ))
    ) * scaleFactor + minScaleOffest).toFloat()
  }

    fun isPortraitMode(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

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