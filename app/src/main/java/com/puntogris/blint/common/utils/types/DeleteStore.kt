package com.puntogris.blint.common.utils.types

sealed class DeleteStore {
    sealed class Success : DeleteStore() {
        data object HasStores : Success()
        data object NoStores : Success()
    }

    data object Failure : DeleteStore()
}

