package com.puntogris.blint.common.data.data_source

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClients @Inject constructor() {

    val firestore = Firebase.firestore
    val auth = Firebase.auth
    val storage = Firebase.storage.reference

    val currentUid: String?
        get() = auth.uid

    val currentUser: FirebaseUser?
        get() = auth.currentUser
}