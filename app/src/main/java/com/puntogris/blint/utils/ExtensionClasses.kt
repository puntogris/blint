package com.puntogris.blint.utils

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.*
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

sealed class EventUi{
    data class EventItem(val event: Event): EventUi()
    data class SeparatorItem(val date: String) : EventUi()
}

sealed class ExportResult{
    object Success: ExportResult()
    class Error(val exception: Exception): ExportResult()
    object InProgress: ExportResult()
}

sealed class EventsDashboard{
    class Success(val data:List<Event>):EventsDashboard()
    class Error(val exception: Exception):EventsDashboard()
    object DataNotFound: EventsDashboard()
}

sealed class UserBusiness(){
    class Success(val data:List<Employee>) : UserBusiness()
    class Error(val exception: Exception): UserBusiness()
    object NotFound: UserBusiness()
    object InProgress: UserBusiness()
}

sealed class NotificationsState{
    class Success(val result:MutableList<Notification>): NotificationsState()
    class Error(val exception: Exception): NotificationsState()
    object CollectionEmpty: NotificationsState()
    class OnDelete(val id: String): NotificationsState()
    sealed class Working: NotificationsState(){
        object LoadFirstBatch: Working()
        object LoadMore: Working()
    }
    object Idle: NotificationsState()

}


sealed class RequestResult{
    object InProgress: RequestResult()
    object Success: RequestResult()
    object Error: RequestResult()
    object NotFound: RequestResult()
}

sealed class JoinBusiness{
    object InProgress: JoinBusiness()
    object Success: JoinBusiness()
    object Error: JoinBusiness()
    object CodeInvalid: JoinBusiness()
    object AlreadyJoined: JoinBusiness()
}

sealed class RegistrationData(){
    object NotFound: RegistrationData()
    class Complete(val username:String, val userCountry: String): RegistrationData()
    object Incomplete: RegistrationData()
    object Error: RegistrationData()
}