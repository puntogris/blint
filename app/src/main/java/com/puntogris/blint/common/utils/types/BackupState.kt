package com.puntogris.blint.common.utils.types

import com.puntogris.blint.R

sealed class BackupState {
    object Loading : BackupState()
    class ShowLastBackup(val lastBackupDate: Long = 0L) : BackupState()
    object BackupSuccess : BackupState()
    class Error(val error: Int = R.string.snack_error_connection_server_try_later) : BackupState()
}