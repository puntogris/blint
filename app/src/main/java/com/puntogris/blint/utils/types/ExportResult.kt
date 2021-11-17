package com.puntogris.blint.utils.types

sealed class ExportResult {
    object Success : ExportResult()
    class Error(val exception: Exception) : ExportResult()
    object InProgress : ExportResult()
}