package com.puntogris.blint.data.repository.user

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.UserData
import com.puntogris.blint.utils.types.SimpleResult
import com.puntogris.blint.utils.types.SyncAccount

interface IUserRepository {
    fun checkIfUserIsLogged(): Boolean
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getCurrentUID(): String
    fun getCurrentUser(): FirebaseUser?
    suspend fun syncAccountFromDatabase(userData: UserData? = null): SyncAccount
    suspend fun getUserBusiness(): List<Business>
}