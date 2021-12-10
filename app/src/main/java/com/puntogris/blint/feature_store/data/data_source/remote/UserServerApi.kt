package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.model.User
import java.io.File
import java.io.FileInputStream

interface UserServerApi {

    suspend fun getUserAccount(authUser: AuthUser): User

    suspend fun sendTicket(ticket: Ticket)

    suspend fun uploadBackup(stream: FileInputStream)

    suspend fun downloadBackup(file: File)

    suspend fun getLastBackupTimestamp(): Long

    suspend fun deleteAccount()
}