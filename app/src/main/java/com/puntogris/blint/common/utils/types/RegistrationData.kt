package com.puntogris.blint.common.utils.types

import com.puntogris.blint.feature_store.domain.model.AuthUser

sealed class RegistrationData {
    object NotFound : RegistrationData()
    class Complete(val authUser: AuthUser) : RegistrationData()
    object Incomplete : RegistrationData()
    object Error : RegistrationData()
}