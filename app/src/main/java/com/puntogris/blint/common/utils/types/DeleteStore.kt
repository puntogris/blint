package com.puntogris.blint.common.utils.types

sealed class DeleteStore {
    sealed class Success : DeleteStore() {
        object HasStores : Success()
        object NoStores : Success()
    }

    object Failure : DeleteStore()
}

