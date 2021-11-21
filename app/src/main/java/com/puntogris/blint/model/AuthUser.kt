package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/*
 Wrapper to a FirebaseUser in order to make it parcelable and use with navigation safe args.
*/
@Keep
@Parcelize
data class AuthUser(
    val name: String = "",
    val uid: String = "",
    val photoUrl: String = "",
    val email: String = ""
) : Parcelable