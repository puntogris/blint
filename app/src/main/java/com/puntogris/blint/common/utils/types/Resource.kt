package com.puntogris.blint.common.utils.types

import com.puntogris.blint.R

sealed class Resource<out T : Any> {
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val error: Int = R.string.snack_an_error_occurred) : Resource<Nothing>()
}
