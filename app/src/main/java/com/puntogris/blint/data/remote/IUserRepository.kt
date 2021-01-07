package com.puntogris.blint.data.remote

import com.puntogris.blint.utils.AuthResult
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
//    suspend fun saveUserToDatabases(roomUser: RoomUser)
//    suspend fun getCurrentUserFromDatabase(): RoomUser
//    fun sendUserSuggestion(message: String)
//    suspend fun editUserPhoneNumber(phoneNumber: String)
}