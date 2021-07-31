package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.encoders.annotations.Encodable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.ONLINE
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity
@Keep
data class Employee(

        @ColumnInfo
        var employeeId: String = "",

        @PrimaryKey(autoGenerate = false)
        var businessId: String = "",

        @ColumnInfo
        var name: String = "",

        @ColumnInfo
        val businessName: String = "",

        @ColumnInfo
        val businessType: String = "",

        @ColumnInfo
        val businessOwner: String = "",

        @ColumnInfo
        val role:String = "",

        @ColumnInfo
        val employeeCreatedAt: Timestamp = Timestamp.now(),

        @ColumnInfo
        val businessCreatedAt: Timestamp = Timestamp.now(),

        @ColumnInfo
        val email:String = "",

        @ColumnInfo
        val businessStatus: String = "",

        @ColumnInfo
        val lastStatusTimestamp: Timestamp = Timestamp.now()

):Parcelable{
        fun isOnlineBusiness() = businessType == ONLINE
        fun iLocalBusiness() = businessType == LOCAL
        fun isAdministrator() = role == ADMINISTRATOR
}
