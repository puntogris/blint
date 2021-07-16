package com.puntogris.blint.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.puntogris.blint.R
import com.puntogris.blint.utils.Constants.WEB_CLIENT_ID
import com.puntogris.blint.utils.showLongSnackBar
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class OneTapLogin @Inject constructor(@ActivityContext private val context: Context) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private var counter = 0
    private var isEnabled = true

    private fun loginCanceled(){
        if (counter >= 3) isEnabled = false
    }

    fun isLoginEnabled() = isEnabled

    private fun createSignInRequest(): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

    fun getSingInCredentials(data: Intent?): SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)

    suspend fun beginSignInResult(): BeginSignInResult =
        oneTapClient.beginSignIn(createSignInRequest()).await()


    fun showSingInUI(activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>){
        oneTapClient.beginSignIn(createSignInRequest())
            .addOnSuccessListener {
                activityResultLauncher.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())
            }
            .addOnFailureListener {
                it.localizedMessage?.let { message ->
                    //integrar esto con las de abajo dealguna forma
                    context.showLongSnackBar(message)
                }
            }
    }

    fun onOneTapException(exception: ApiException){
        when (exception.statusCode) {
            CommonStatusCodes.CANCELED -> {
                loginCanceled()
            }
            CommonStatusCodes.NETWORK_ERROR ->
                context.showLongSnackBar(context.getString(R.string.snack_network_problems))
            else ->
                context.showLongSnackBar(context.getString(R.string.snack_technical_problems))
        }
    }
}