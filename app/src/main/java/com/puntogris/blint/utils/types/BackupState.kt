package com.puntogris.blint.utils.types

sealed class BackupState {
    object Success : BackupState()
    class Error(val exception: Exception) : BackupState()
    class InProgress(val progress: Long = 0L) : BackupState()
}
