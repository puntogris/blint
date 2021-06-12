package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductWithRecord(val product: Product, val record: Record = Record()): Parcelable