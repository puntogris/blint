package com.puntogris.blint.feature_store.data.data_source.remote

import com.google.firebase.Timestamp
import com.puntogris.blint.common.utils.Constants

class FirestoreUser(
    val uid: String = "",

    val name: String = "",

    val photoUrl: String = "",

    val email: String = "",

    val currentBusinessId: String = "",

    val appTier: String = Constants.BASIC_PLAN,

    val createdAt: Timestamp = Timestamp.now()
)
