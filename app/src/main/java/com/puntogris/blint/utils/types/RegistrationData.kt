package com.puntogris.blint.utils.types

import com.puntogris.blint.model.UserData

sealed class RegistrationData {
    object NotFound : RegistrationData()
    class Complete(val userData: UserData) : RegistrationData()
    object Incomplete : RegistrationData()
    object Error : RegistrationData()
}