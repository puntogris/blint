package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.puntogris.blint.utils.Constants.ONLINE

@Entity
data class RoomUser(

        @PrimaryKey(autoGenerate = false)
        val userId:String = "1",

        @ColumnInfo
        var country: String = "",

        @ColumnInfo
        var username:String = "",

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

        @ColumnInfo
        val currentBusinessStatus: String = ""
){
        fun currentBusinessIsOnline() = currentBusinessType == ONLINE

}