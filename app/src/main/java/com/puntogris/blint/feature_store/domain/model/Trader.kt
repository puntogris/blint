package com.puntogris.blint.feature_store.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.puntogris.blint.common.utils.Constants
import kotlinx.parcelize.Parcelize

/*
 A Trader is a generic wrapper for either a Client or Supplier.
*/
@Keep
@Parcelize
class Trader(
    val traderId: String,
    val traderType: String,
    val traderName: String
) : Parcelable

fun Client.toTrader(): Trader {
    return Trader(
        traderType = Constants.CLIENT,
        traderId = clientId,
        traderName = name
    )
}

fun Supplier.toTrader(): Trader {
    return Trader(
        traderType = Constants.SUPPLIER,
        traderId = supplierId,
        traderName = companyName
    )
}