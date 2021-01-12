package com.puntogris.blint.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.puntogris.blint.utils.Constants.WEB_CLIENT_ID
import com.puntogris.blint.utils.showLongSnackBar
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.qualifiers.ActivityContext
import java.lang.Exception
import javax.inject.Inject

class OneTapLogin @Inject constructor(@ActivityContext private val context: Context) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private var counter = 0
    private var isEnabled = true

    fun loginCanceled(){
        if (counter > 3) isEnabled = false else counter += 1
    }

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

    fun singOut(){
        oneTapClient.signOut()
    }

    fun showSingInUI(activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>){
        if (isEnabled) {
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
        else
            context.showLongSnackBar("Debido a multiples intentos de ingreso consecutivos, se deshabilito el ingreso a la app momentaneamente.")
    }

    fun onOneTapException(exception: ApiException){
        when (exception.statusCode) {
            CommonStatusCodes.CANCELED -> loginCanceled()
            CommonStatusCodes.NETWORK_ERROR ->
                context.showLongSnackBar("Se encontro un problema con la conexion. Revisa tu red y intenta nuevamente.")
            else ->
                context.showLongSnackBar("Estamos teniendo dificultades tecnicas. Intenta nuevamente en un rato.")
        }
    }
}