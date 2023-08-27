package com.puntogris.blint.common.utils.types

sealed class ExportResult {
    data object Success : ExportResult()
    class Error(val exception: Exception) : ExportResult()
    data object InProgress : ExportResult()
}