package com.pepe.mycars.app.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.FireStoreCollection.USER
import com.pepe.mycars.app.utils.FireStoreCollection.USER_GUEST
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.ResourceOld
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale

class UserRepositoryImpl(
    private val fireStoreDatabase: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val appPreferences: SharedPreferences,
    val gson: Gson,
) : UserRepository {

    private var firebaseUser: FirebaseUser? = null

    override fun getLoggedUserData(): Flow<ResourceOld<UserModel>> = flow {
        emit(ResourceOld.Loading())
        firebaseUser = auth.currentUser
        try {
            if (firebaseUser != null) {
                val uId = firebaseUser!!.uid
                val autoLogin = appPreferences.getBoolean("autoLogin", false)
                val dbCollection = if (firebaseUser!!.isAnonymous) USER_GUEST else USER
                var snapshot =
                    fireStoreDatabase.collection(dbCollection).document(uId).get().await()
                val providerType = appPreferences.getString("provider", "") ?: ""

                if (snapshot.exists()) {
                    fireStoreDatabase.collection(dbCollection).document(uId)
                        .update("autoLogin", autoLogin)
                        .await()
                    snapshot =
                        fireStoreDatabase.collection(dbCollection).document(uId).get().await()
                    val userModel = snapshot.toObject(UserModel::class.java)

                    if (userModel != null) {
                        if (providerType.isNotEmpty() && userModel.providerType != providerType) {
                            fireStoreDatabase.collection(dbCollection).document(uId)
                                .update("providerType", providerType).await()
                        }
                        emit(ResourceOld.Success(data = userModel))
                    } else {
                        emit(ResourceOld.Error(message = "Unknown error"))
                    }

                } else {
                    val email = firebaseUser!!.email ?: ""
                    val country = Locale.getDefault().country
                    if (dbCollection == USER_GUEST) {
                        val snapshot = fireStoreDatabase.collection(dbCollection).get().await()
                        val name = "Guest(" + snapshot.size() + ")"
                        val userModel =
                            UserModel(name, email, true, country, providerType, uId, autoLogin)
                        fireStoreDatabase.collection(USER_GUEST).document(uId).set(userModel)
                            .await()
                        emit(ResourceOld.Success(data = userModel))
                    } else {
                        val name = appPreferences.getString("userName", null) ?: ""
                        val userModel =
                            UserModel(name, email, true, country, providerType, uId, autoLogin)
                        fireStoreDatabase.collection(dbCollection).document(uId).set(userModel)
                            .await()
                        emit(ResourceOld.Success(data = userModel))
                    }
                }
            } else {
                emit(ResourceOld.Error(message = "Not logged", data = null))
            }
        } catch (e: HttpException) {
            emit(ResourceOld.Error(message = e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                ResourceOld.Error(
                    message = e.localizedMessage ?: "Check Your Internet Connection"
                )
            )
        } catch (e: Exception) {
            emit(ResourceOld.Error(message = e.localizedMessage ?: "Unknown exception"))
        }
    }

    override fun getUserProviderType(): Flow<ResourceOld<String>> = flow {
        emit(ResourceOld.Loading())
        if (firebaseUser!!.isAnonymous) {
            emit(ResourceOld.Success(data = ACCOUNT_PROVIDER_ANONYMOUS))
        } else {
            try {
                val snapshot =
                    fireStoreDatabase.collection(USER).document(firebaseUser!!.uid).get().await()
                if (snapshot.exists()) {
                    val userModel = snapshot.toObject(UserModel::class.java)
                    val providerType = userModel!!.providerType
                    emit(ResourceOld.Success(data = providerType))
                } else {
                    emit(ResourceOld.Error(message = "Unknown Error"))
                }
            } catch (e: HttpException) {
                emit(ResourceOld.Error(message = e.localizedMessage ?: "Unknown Error"))
            } catch (e: IOException) {
                emit(
                    ResourceOld.Error(
                        message = e.localizedMessage ?: "Check Your Internet Connection"
                    )
                )
            } catch (e: Exception) {
                emit(ResourceOld.Error(message = e.localizedMessage ?: "Unknown exception"))
            }
        }


    }
}