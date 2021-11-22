package com.puntogris.blint.utils.types

import com.puntogris.blint.R

typealias SimpleRepoResult = RepoResult<Unit>

sealed class RepoResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : RepoResult<T>()
    data class Error(val error: Int = R.string.snack_an_error_occurred) : RepoResult<Nothing>()
    object InProgress : RepoResult<Nothing>()
}
