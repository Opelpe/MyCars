package com.pepe.mycars.app.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.FireStoreCollection.USER
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_EMAIL
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_GOOGLE
import com.pepe.mycars.app.utils.state.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val fireStoreDatabase: FirebaseFirestore,
    private val appPreferences: SharedPreferences
) : AuthRepository {
    override fun register(
        email: String,
        password: String,
        name: String,
        autoLogin: Boolean
    ): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val providerType = result.credential?.provider ?: ACCOUNT_PROVIDER_EMAIL
            appPreferences.edit().putString("provider", providerType).apply()
            appPreferences.edit().putString("userName", name).apply()
            appPreferences.edit().putBoolean("autoLogin", autoLogin).apply()

            emit((result.user?.let {
                AuthState.Success(it)
            }!!))

        } catch (e: HttpException) {
            emit(AuthState.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                AuthState.Error(e.localizedMessage ?: "Check Your Internet Connection")
            )
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: ""))
        }
    }

    override fun registerWithGoogle(
        authCredential: AuthCredential,
        name: String,
        email: String,
        autoLogin: Boolean
    ): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        try {
            val result = firebaseAuth.signInWithCredential(authCredential).await()
            val providerType = result.credential?.provider ?: ACCOUNT_PROVIDER_GOOGLE
            appPreferences.edit().putString("provider", providerType).apply()
            appPreferences.edit().putString("userName", name).apply()
            appPreferences.edit().putBoolean("autoLogin", autoLogin).apply()

            emit((result.user?.let {
                AuthState.Success(it)
            }!!))

        } catch (e: HttpException) {
            emit(AuthState.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(AuthState.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(AuthState.Error(e.localizedMessage ?: ""))
        }
    }

    override fun registerAsGuest(autoLogin: Boolean): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        try {
            val result = firebaseAuth.signInAnonymously().await()
            val providerType = result.credential?.provider ?: ACCOUNT_PROVIDER_ANONYMOUS
            appPreferences.edit().putString("provider", providerType).apply()
            appPreferences.edit().putString("userName", null).apply()
            appPreferences.edit().putBoolean("autoLogin", autoLogin).apply()

            result.user?.let {
                emit(AuthState.Success(it))
            }

        } catch (e: HttpException) {
            Log.d("registerGuest", "register as guest exception: " + e)
            emit(AuthState.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            Log.d("registerGuest", "register as guest exception: " + e)
            emit(AuthState.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            Log.d("registerGuest", "register as guest exception: " + e)
            emit(AuthState.Error(e.localizedMessage ?: ""))
        }
    }

    override fun login(email: String, password: String, autoLogin: Boolean): Flow<AuthState> =
        flow {
            emit(AuthState.Loading)
                        try {
                            val snapshot = fireStoreDatabase.collection(USER).whereEqualTo("email", email).get().await()

                            if (snapshot.documents.size > 0) {
                                val userModel: UserModel? = snapshot.documents[0].toObject(UserModel::class.java)
                                val providerType = userModel!!.providerType

                                if (providerType == ACCOUNT_PROVIDER_GOOGLE) {
                                    emit(AuthState.Error("Try with google sign in"))
                                } else {
                                    val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                                    emit((result.user?.let {
                                        appPreferences.edit().putString("provider", providerType).apply()
                                        AuthState.Success(it)
                                    }!!))
                                }
                            } else {
                                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                                val providerType = result.credential?.provider ?: ACCOUNT_PROVIDER_EMAIL
                                emit((result.user?.let {
                                    appPreferences.edit().putString("provider", providerType).apply()
                                    AuthState.Success(it)
                                }!!))
                            }

                        } catch (e: HttpException) {
                            emit(AuthState.Error(e.localizedMessage ?: "Unknown Error"))
                        } catch (e: IOException) {
                            emit(
                                AuthState.Error(e.localizedMessage ?: "Check Your Internet Connection")
                            )
                        } catch (e: Exception) {
                            emit(AuthState.Error(e.localizedMessage ?: ""))
                        }

                }

    override fun logOut() {
        firebaseAuth.signOut()
    }

    override fun getLoggedUser(): Flow<AuthState> = flow {
        emit(AuthState.Loading)
        try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                emit(AuthState.Success(user))
            } else {
                emit(AuthState.Error("Log in"))
            }
        } catch (e: Exception) {
            emit(AuthState.Error("Unknown error: " + e.message))
        }
    }

}