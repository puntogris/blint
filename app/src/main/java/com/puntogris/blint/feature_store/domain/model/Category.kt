package com.puntogris.blint.feature_store.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@Keep
data class Category(

    @PrimaryKey(autoGenerate = false)
    var categoryName: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var storeId: String = ""

) : Parcelable