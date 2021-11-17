package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class OrderWithRecords(
    @Embedded var order: Order = Order(),

    @Relation(
        entity = Record::class,
        parentColumn = "orderId",
        entityColumn = "recordId",
        associateBy = Junction(OrderRecordCrossRef::class)
    )
    var records: List<FirestoreRecord> = listOf(),

    @Relation(
        entity = Debt::class,
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    var debt: FirestoreDebt? = null
) : Parcelable
