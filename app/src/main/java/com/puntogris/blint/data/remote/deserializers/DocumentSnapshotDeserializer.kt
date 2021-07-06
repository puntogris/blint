package com.puntogris.blint.data.remote.deserializers

import com.google.firebase.firestore.DocumentSnapshot

internal interface DocumentSnapshotDeserializer<T> :
    Deserializer<DocumentSnapshot, T>
