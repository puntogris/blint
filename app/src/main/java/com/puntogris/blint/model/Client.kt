package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
):Parcelable