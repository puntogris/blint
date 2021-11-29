package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.data.data_source.toUserEntity
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.model.User
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class FirebaseUserApi(
    private val firebase: FirebaseClients
) : UserServerApi {

    private fun getUserBackupStorageQuery() =
        firebase.storage.child("${Constants.USERS_PATH}/${firebase.currentUid}/${Constants.BACKUP_PATH}_${BuildConfig.VERSION_NAME}")

    override suspend fun getUserAccount(authUser: AuthUser): User {

        val userRef = firebase.firestore
            .collection(Constants.USERS_COLLECTION)
            .document(requireNotNull(firebase.currentUid))

        val user = userRef
            .get()
            .await()
            .toObject(User::class.java)

        return user ?: authUser.toUserEntity().also {
            userRef.set(it).await()
        }
    }

    override suspend fun sendTicket(ticket: Ticket) {
        firebase.firestore
            .collection(Constants.TICKETS_COLLECTION)
            .document(ticket.ticketId)
            .set(ticket)
            .await()
    }

    override suspend fun uploadBackup(stream: FileInputStream) {
         getUserBackupStorageQuery().putStream(stream).await()
    }

    override suspend fun downloadBackup(file: File) {
        getUserBackupStorageQuery().getFile(file).await()
    }

    override suspend fun getLastBackupTimestamp(): Long {
        return getUserBackupStorageQuery().metadata.await().creationTimeMillis
    }
}