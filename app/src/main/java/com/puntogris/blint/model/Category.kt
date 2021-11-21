package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@Keep
data class Category(

    @PrimaryKey(autoGenerate = false)
    var categoryName: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var businessId: String = ""

) : Parcelable