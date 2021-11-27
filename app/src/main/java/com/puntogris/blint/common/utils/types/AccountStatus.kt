package com.puntogris.blint.common.utils.types

import com.puntogris.blint.feature_store.domain.model.Business

sealed class AccountStatus {
    class OutOfSync(val affectedBusinesses: List<Business>) : AccountStatus()
    class Synced(val hasBusiness: Boolean) : AccountStatus()
    object Error : AccountStatus()
}