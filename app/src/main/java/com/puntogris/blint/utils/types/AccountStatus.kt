package com.puntogris.blint.utils.types

import com.puntogris.blint.model.Business

sealed class AccountStatus {
    class OutOfSync(val affectedBusinesses: List<Business>) : AccountStatus()
    class Synced(val hasBusiness: Boolean) : AccountStatus()
    object Error : AccountStatus()
}