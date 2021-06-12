package com.puntogris.blint.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem (
                  val timestamp: Timestamp = Timestamp.now(),

                  var amount: Int = 0,

                  val productID: Int = 0,

                  val productName:String = "",

                  val author: String = "",

                  var businessId:String = "",

                  var productUnitPrice: Int = 0,

                  val parentRecordId: String = ""):Parcelable