package com.puntogris.blint.feature_store.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import org.threeten.bp.OffsetDateTime

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

    @ColumnInfo(name = "user_name")
    val name: String = "",

    @ColumnInfo
    val photoUrl: String = "",

    @ColumnInfo
    val email: String = "",

    @ColumnInfo
    val currentStoreId: String = "",

    @ColumnInfo
    val appTier: String = "BASIC",

    @ColumnInfo
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
