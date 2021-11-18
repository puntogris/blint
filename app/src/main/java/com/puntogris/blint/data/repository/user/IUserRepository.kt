package com.puntogris.blint.data.repository.user

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.utils.types.SimpleResult
import com.puntogris.blint.utils.types.SyncAccount

interface IUserRepository {

    fun checkIfUserIsLogged(): Boolean

    suspend fun sendReportToFirestore(message: String): SimpleResult

    fun getCurrentUID(): String

    fun getCurrentUser(): FirebaseUser?

    suspend fun syncUserAccount(authUser: AuthUser? = null): SyncAccount

    suspend fun getUserBusiness(): List<Business>
}