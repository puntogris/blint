package com.puntogris.blint.utils.types

sealed class SyncAccount {
    sealed class Success : SyncAccount() {
        object HasBusiness : Success()
        object BusinessNotFound : Success()
    }

    class Error(val exception: Exception) : SyncAccount()
}
