package com.puntogris.blint.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.puntogris.blint.BuildConfig
import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.feature_auth.data.data_source.FirebaseAuthApi
import com.puntogris.blint.feature_auth.data.data_source.GoogleSingInApi
import com.puntogris.blint.feature_auth.data.repository.AuthRepositoryImpl
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.remote.AuthServerApi
import com.puntogris.blint.feature_store.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthModule {

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        sharedPreferences: SharedPreferences,
        authServerApi: AuthServerApi,
        googleSingInApi: GoogleSingInApi,
        dispatcher: DispatcherProvider
    ): AuthRepository {
        return AuthRepositoryImpl(
            sharedPreferences,
            authServerApi,
            googleSingInApi,
            dispatcher
        )
    }

    @Provides
    @Singleton
    fun provideAuthServerApi(firebaseClients: FirebaseClients): AuthServerApi {
        return FirebaseAuthApi(firebaseClients)
    }
}