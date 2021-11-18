package com.puntogris.blint.utils.types

import com.puntogris.blint.model.AuthUser

sealed class RegistrationData {
    object NotFound : RegistrationData()
    class Complete(val authUser: AuthUser) : RegistrationData()
    object Incomplete : RegistrationData()
    object Error : RegistrationData()
}