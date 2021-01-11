package com.puntogris.blint.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.*
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.utils.Constants.RC_ONE_TAP
import com.puntogris.blint.utils.Constants.WEB_CLIENT_ID
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class OneTapLogin @Inject constructor(@ActivityContext private val context: Context) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val parentView: View = (context as Activity).findViewById(android.R.id.content)
    private var counter = 0
    private var enabled = true

    fun isEnabled() = enabled

    fun loginCanceled(){
        if (counter > 3) enabled = false else counter += 1
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

    fun singIn(fragment:Fragment){
        val signUpRequest = createSignInRequest()
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener { result ->
                try {
                    fragment.startIntentSenderForResult(
                        result.pendingIntent.intentSender, RC_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }

            }.addOnFailureListener { e ->
                Log.e(TAG, "No saved credentials: ${e.localizedMessage}")
                Snackbar.make(parentView, e.localizedMessage, Snackbar.LENGTH_LONG).show()
            }
    }

    fun getSingInCredentials(data: Intent?): SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)

    fun singOut(){
        oneTapClient.signOut()
    }
}