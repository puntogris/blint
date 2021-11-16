package com.puntogris.blint.data.data_source.remote.deserializers

import com.google.firebase.firestore.DocumentSnapshot

internal interface DocumentSnapshotDeserializer<T> :
    Deserializer<DocumentSnapshot, T>
