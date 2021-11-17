package com.puntogris.blint.utils.types

sealed class RepoResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : RepoResult<T>()
    data class Error(val exception: Exception) : RepoResult<Nothing>()
    object InProgress : RepoResult<Nothing>()
}
