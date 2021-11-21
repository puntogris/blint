package com.puntogris.blint.model

import android.os.Parcelable
import com.puntogris.blint.utils.Constants
import kotlinx.parcelize.Parcelize

/*
 A Trader is a generic wrapper for either a Client or Supplier.
*/
@Parcelize
class Trader(
    val traderId: String,
    val traderType: String,
    val traderName: String
): Parcelable

fun Client.toTrader(): Trader{
    return Trader(
        traderType = Constants.CLIENT,
        traderId = clientId,
        traderName = name
    )
}

fun Supplier.toTrader(): Trader{
    return Trader(
        traderType = Constants.SUPPLIER,
        traderId = supplierId,
        traderName = companyName
    )
}