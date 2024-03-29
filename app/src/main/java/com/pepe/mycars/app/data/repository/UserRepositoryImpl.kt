package com.pepe.mycars.app.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.FireStoreCollection.USER
import com.pepe.mycars.app.utils.FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.state.UserModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale

class UserRepositoryImpl(
    private val fireStoreDatabase: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : UserRepository {

    override fun getLoggedUserData(): Flow<UserModelState> = flow {
        emit(UserModelState.Loading)
        val firebaseUser = auth.currentUser
        try {
            if (firebaseUser != null) {
                val uId = firebaseUser.uid
                val autoLogin = sharedPreferences.getBoolean("autoLogin", false)
                val isAnonymous = firebaseUser.isAnonymous
                var document = fireStoreDatabase.collection(USER).document(uId).get().await()
                val providerType = sharedPreferences.getString("provider", "") ?: ""

                if (document.exists()) {
                    fireStoreDatabase.collection(USER).document(uId)
                        .update("autoLogin", autoLogin)
                        .await()
                    document = fireStoreDatabase.collection(USER).document(uId).get().await()
                    val userModel = document.toObject(UserModel::class.java)

                    if (userModel != null) {
                        if (providerType.isNotEmpty() && userModel.providerType != providerType) {
                            fireStoreDatabase.collection(USER).document(uId)
                                .update("providerType", providerType).await()
                        }
                        emit(UserModelState.Success(userModel))
                    } else {
                        emit(UserModelState.Error("Unknown error"))
                    }

                } else {
                    val email = firebaseUser.email ?: ""
                    val country = Locale.getDefault().country
                    if (isAnonymous) {
                        val collection = fireStoreDatabase.collection(USER).whereEqualTo("providerType", ACCOUNT_PROVIDER_ANONYMOUS).get().await()
                        val name = "Guest(" + collection.size() + ")"
                        val userModel = UserModel(name, email, true, country, providerType, uId, autoLogin)
                        fireStoreDatabase.collection(USER).document(uId).set(userModel).await()
                        emit(UserModelState.Success(userModel))
                    } else {
                        val name = sharedPreferences.getString("userName", null) ?: ""
                        val userModel = UserModel(name, email, true, country, providerType, uId, autoLogin)
                        fireStoreDatabase.collection(USER).document(uId).set(userModel).await()
                        emit(UserModelState.Success(userModel))
                    }
                }
            } else {
                emit(UserModelState.Error("Not logged"))
            }
        } catch (e: HttpException) {
            emit(UserModelState.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(UserModelState.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(UserModelState.Error(e.localizedMessage ?: "Unknown exception"))
        }
    }

    override fun getUserProviderType(): String {
        val firebaseUser = auth.currentUser
        return if (firebaseUser!!.isAnonymous) {
            ACCOUNT_PROVIDER_ANONYMOUS
        } else {
            sharedPreferences.getString("provider", "") ?: ""
        }
    }
}