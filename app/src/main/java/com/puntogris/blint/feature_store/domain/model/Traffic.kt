package com.puntogris.blint.feature_store.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity
class Traffic(

    @PrimaryKey
    val date: String = LocalDate.now().toString(),

    @ColumnInfo
    var sales: Float = 0F,

    @ColumnInfo
    var purchases: Float = 0F,

    @ColumnInfo
    var storeId: String = ""
){
    fun revenue() = sales - purchases
}