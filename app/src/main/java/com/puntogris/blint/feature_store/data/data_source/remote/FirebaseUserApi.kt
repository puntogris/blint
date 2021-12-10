package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.BuildConfig
import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.feature_store.data.data_source.toFirestoreUser
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
        firebase.storage.child("users/${firebase.currentUid}/backup_${BuildConfig.VERSION_NAME}")

    override suspend fun getUserAccount(authUser: AuthUser): User {

        val userRef = firebase.firestore
            .collection(USERS_COLLECTION)
            .document(requireNotNull(firebase.currentUid))

        val snap = userRef
            .get()
            .await()

        return if (snap.exists()) {
            DocumentSnapshotUserDeserializer().deserialize(snap)
        } else {
            userRef.set(authUser.toFirestoreUser()).await()
            authUser.toUserEntity()
        }
    }

    override suspend fun sendTicket(ticket: Ticket) {
        firebase.firestore
            .collection(TICKETS_COLLECTION)
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

    override suspend fun deleteAccount() {
        firebase.firestore
            .collection(USERS_COLLECTION)
            .document(requireNotNull(firebase.currentUid))
            .delete()
            .await()
    }

    companion object {
        const val USERS_COLLECTION = "users"
        const val TICKETS_COLLECTION = "tickets"
    }
}