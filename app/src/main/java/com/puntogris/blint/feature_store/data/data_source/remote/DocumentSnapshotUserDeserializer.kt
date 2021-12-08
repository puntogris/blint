package com.puntogris.blint.feature_store.data.data_source.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.common.utils.toOffsetDateTime
import com.puntogris.blint.feature_store.domain.model.User

class DocumentSnapshotUserDeserializer {

    fun deserialize(input: DocumentSnapshot): User {
        val uid = input.getString("uid") ?: ""

        val name = input.getString("name") ?: ""

        val photoUrl = input.getString("photoUrl") ?: ""

        val email = input.getString("email") ?: ""

        val currentBusinessId = input.getString("currentBusinessId") ?: ""

        val appTier = input.getString("appTier") ?: "BASIC"

        val createdAt = input.getTimestamp("createdAt") ?: Timestamp.now()

        return User(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            email = email,
            currentBusinessId = currentBusinessId,
            appTier = appTier,
            createdAt = createdAt.toOffsetDateTime()
        )
    }
}
