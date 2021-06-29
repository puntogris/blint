package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Category(
    @PrimaryKey
    var categoryId: String = "",

    @ColumnInfo
    val name:String,

    @ColumnInfo
    var businessId: String = ""):Parcelable