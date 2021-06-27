package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Entity
class RoomUser(
        @PrimaryKey(autoGenerate = false)
        val userId:String = "1",

        @ColumnInfo
        val country: String = "",

        @ColumnInfo
        val username:String = "",

        @ColumnInfo
        val currentUid:String = "",

        @ColumnInfo
        val currentBusinessId:String = "",

        @ColumnInfo
        val currentBusinessType:String = "",

        @ColumnInfo
        val currentBusinessName:String = "",

        @ColumnInfo
        val currentBusinessOwner:String = "",
){
        fun typeIsOnline() = currentBusinessType == "ONLINE"

}