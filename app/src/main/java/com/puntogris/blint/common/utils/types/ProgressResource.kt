package com.puntogris.blint.common.utils.types

import com.puntogris.blint.R

typealias SimpleProgressResource = ProgressResource<Unit>

sealed class ProgressResource<out T : Any> {
    data class Success<out T : Any>(val data: T) : ProgressResource<T>()
    data class Error(val error: Int = R.string.snack_an_error_occurred) :
        ProgressResource<Nothing>()

    object InProgress : ProgressResource<Nothing>()
}
