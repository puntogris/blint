package com.puntogris.blint.utils.types

sealed class SimpleResult {
    object Success : SimpleResult()
    object Failure : SimpleResult()
}
