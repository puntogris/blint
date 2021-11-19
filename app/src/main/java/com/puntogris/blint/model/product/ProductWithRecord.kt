package com.puntogris.blint.model.product

import android.os.Parcelable
import com.puntogris.blint.model.order.Record
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductWithRecord(
    val product: Product,
    val record: Record = Record()
) : Parcelable