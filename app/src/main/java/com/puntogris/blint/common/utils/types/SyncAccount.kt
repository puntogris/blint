package com.puntogris.blint.common.utils.types

sealed class SyncAccount {
    sealed class Success : SyncAccount() {
        data object HasBusiness : Success()
        data object BusinessNotFound : Success()
    }

    class Error(val exception: Exception) : SyncAccount()
}
