package com.puntogris.blint.feature_store.data.data_source.remote

import com.google.firebase.Timestamp

class FirestoreUser(
    val uid: String = "",

    val name: String = "",

    val photoUrl: String = "",

    val email: String = "",

    val currentBusinessId: String = "",

    val appTier: String = "BASIC",

    val createdAt: Timestamp = Timestamp.now()
)
