package com.puntogris.blint.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

    // Zip filesBeingZipped into zipDirectory using standard File IO, and name it zipFileName.
    fun zipDatabases(filesBeingZipped: List<File>, zipDirectory: String, zipFileName: String): Boolean {
        var success = true
        try {
            val BUFFER = 80000
            var origin: BufferedInputStream
            val dest = FileOutputStream("$zipDirectory/$zipFileName")
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)
            for (fileBeingZipped in filesBeingZipped) {
                val fi = FileInputStream(fileBeingZipped)
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(
                    fileBeingZipped.path
                        .substring(fileBeingZipped.path.lastIndexOf("/") + 1)
                )
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
            out.close()
        } catch (e: Exception) {
//            showErrorToastOnUiThread(e)
//            showLogError(LOG_TAG, e, "")
            success = false
        }
        return success
    }
}