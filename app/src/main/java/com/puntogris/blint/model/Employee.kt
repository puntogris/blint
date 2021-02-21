package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Employee(

        @PrimaryKey(autoGenerate = false)
        var employeeId: String = "",

        @ColumnInfo
        var businessId: String = "",

        @ColumnInfo
        val name: String = "",

        @ColumnInfo
        val businessName: String = "",

        @ColumnInfo
        val businessType: String = "",

        @ColumnInfo
        val owner: String = "",

        @ColumnInfo
        val role:String = "",

        @ColumnInfo
        val employeeTimestamp: Timestamp = Timestamp.now(),

        @ColumnInfo
        val businessTimestamp: Timestamp = Timestamp.now(),

        @ColumnInfo
        val email:String = ""
):Parcelable
