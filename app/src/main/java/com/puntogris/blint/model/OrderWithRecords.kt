package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class OrderWithRecords(
    @Embedded var order: Order = Order(),

    @Relation(
        parentColumn = "orderId",
        entityColumn = "recordId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    var records: List<FirestoreRecord> = listOf()
):Parcelable
