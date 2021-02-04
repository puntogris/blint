package com.puntogris.blint.utils

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Event
import java.lang.Exception

sealed class StringValidator{
    class Valid(val value: String): StringValidator()
    class NotValid(val error: String): StringValidator()

    companion object{
        fun from(text: String, validLength: Int = 3, allowSpecialChars: Boolean = false): StringValidator{
            return when{
                text.isBlank() ->
                    NotValid("El nombre no puede estar vacio.")
                !allowSpecialChars && text.containsInvalidCharacters() ->
                    NotValid("El nombre no puede contener caracteres especiales.")
                text.isLengthInvalid(validLength) ->
                    NotValid("El nombre tiene que tener al menos 3 letras.")
                else -> Valid(text)
            }
        }
    }
}

sealed class AuthResult{
    class Success(val user: FirebaseUser) : AuthResult()
    class Error(val exception: Exception): AuthResult()
    object InProgress: AuthResult()
}

sealed class SimpleResult{
    object Success: SimpleResult()
    object Failure: SimpleResult()
}

sealed class RepoResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : RepoResult<T>()
    data class Error(val exception: Exception) : RepoResult<Nothing>()
    object InProgress: RepoResult<Nothing>()
}

sealed class BusinessData{
    object Exists: BusinessData()
    object NotExists: BusinessData()
    class Error(val exception: Exception): BusinessData()
}

sealed class EventUi{
    data class EventItem(val event: Event): EventUi()
    data class SeparatorItem(val date: String) : EventUi()
}