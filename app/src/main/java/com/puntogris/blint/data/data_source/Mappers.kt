package com.puntogris.blint.data.data_source

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User

fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        name = requireNotNull(displayName),
        uid = uid,
        photoUrl = requireNotNull(photoUrl).toString(),
        email = requireNotNull(email)
    )
}

fun AuthUser.toUserEntity(): User {
    return User(
        uid = uid,
        name = name,
        photoUrl = photoUrl,
        email = email
    )
}