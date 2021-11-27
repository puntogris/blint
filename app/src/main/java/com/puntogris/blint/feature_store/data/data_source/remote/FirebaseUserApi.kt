package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.data.data_source.toUserEntity
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseUserApi(
    private val firebase: FirebaseClients
) : UserServerApi {

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

}