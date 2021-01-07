package com.puntogris.blint.utils

import com.google.firebase.auth.FirebaseUser

sealed class StringValidator{
    class Valid(val value: String): StringValidator()
    class NotValid(val error: String): StringValidator()

    companion object{
        fun from(text: String, validLength: Int = 3, allowSpecialChars: Boolean = true): StringValidator{
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
    class Error(val exception: String): AuthResult()
    object InProgress: AuthResult()
}