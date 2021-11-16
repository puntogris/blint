package com.puntogris.blint.utils

import androidx.annotation.StringRes
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.R
import com.puntogris.blint.model.*
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

sealed class StringValidator{
    class Valid(val value: String): StringValidator()
    class NotValid(@StringRes val error: Int): StringValidator()

    companion object{
        fun from(text: String, minLength: Int = 3, maxLength:Int = 20, allowSpecialChars: Boolean = false, isName:Boolean = false): StringValidator{
            return when{
                text.isBlank() ->
                    NotValid(if (isName) R.string.snack_name_empty else R.string.snack_text_empty)
                !allowSpecialChars && text.containsInvalidCharacters() ->
                    NotValid(
                        if (isName) R.string.snack_name_cant_contain_special_chars
                        else R.string.snack_the_text_cant_contain_special_chars)
                text.length !in (minLength..maxLength) ->
                    NotValid(
                        if (isName) R.string.snack_name_min_max_length
                        else R.string.snack_text_min_max_length)
                else -> Valid(text)
            }
        }
    }
}

sealed class AccountStatus{
    class OutOfSync(val affectedBusinesses: List<Employee>): AccountStatus()
    class Synced(val hasBusiness: Boolean): AccountStatus()
    object Error:AccountStatus()
}

sealed class DeleteBusiness{
    sealed class Success: DeleteBusiness(){
        object HasBusiness:Success()
        object NoBusiness:Success()
    }
    object Failure: DeleteBusiness()
}

sealed class BackupState{
    object Success: BackupState()
    class Error(val exception: Exception): BackupState()
    class InProgress(val progress: Long = 0L): BackupState()
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

sealed class SyncAccount{
    sealed class Success : SyncAccount(){
        object HasBusiness:Success()
        object BusinessNotFound:Success()
    }
    class Error(val exception: Exception): SyncAccount()
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

sealed class RegistrationData{
    object NotFound: RegistrationData()
    class Complete(val userData: UserData): RegistrationData()
    object Incomplete: RegistrationData()
    object Error: RegistrationData()
}

sealed class SearchText{
    class QrCode(val text: String): SearchText()
    class Name(val text: String): SearchText()
    class InternalCode(val text: String): SearchText()
    class Category(val text:String): SearchText()
    fun getData(): String{
        return when(this){
            is InternalCode -> text
            is Name -> text
            is QrCode -> text
            is Category -> text
        }
    }
}