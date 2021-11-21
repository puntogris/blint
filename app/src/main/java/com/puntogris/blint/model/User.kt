package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

/*
 localReferenceId is to make sure we only have one user saved locally and override it always,
 also it make it easier to join with other tables to get the currentBusinessId because
 we know that always the user will be under the same local id, as the uid will always change.
*/

@Entity
data class User(

    @PrimaryKey(autoGenerate = false)
    @get:Exclude val localReferenceId: String = "1",

    @ColumnInfo
    val uid: String = "",

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val photoUrl: String = "",

    @ColumnInfo
    val email: String = "",

    @ColumnInfo
    val currentBusinessId: String = "",

    @ColumnInfo
    val appTier: String = "BASIC",

    @ColumnInfo
    val createdAt: Timestamp = Timestamp.now()
)