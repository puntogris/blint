package com.puntogris.blint.utils.types

sealed class DeleteBusiness {
    sealed class Success : DeleteBusiness() {
        object HasBusiness : Success()
        object NoBusiness : Success()
    }

    object Failure : DeleteBusiness()
}

