package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Category(
    @PrimaryKey
    var categoryId: String = "",

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var businessId: String = "",

    @Ignore @get:Exclude var selected: Boolean = false
    ):Parcelable