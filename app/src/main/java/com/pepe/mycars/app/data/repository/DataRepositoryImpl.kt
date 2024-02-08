package com.pepe.mycars.app.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.utils.FireStoreCollection.REFILLS
import com.pepe.mycars.app.utils.FireStoreCollection.USER
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

class DataRepositoryImpl(
    private val fireStoreDatabase: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val appPreferences: SharedPreferences
) : DataRepository {

    override fun getUserItems(): Flow<ItemModelState> = flow {
        emit(ItemModelState.Loading)

        val firebaseUser = auth.currentUser

        try {
            if (firebaseUser != null){
                val uId = firebaseUser.uid
                val refillData = fireStoreDatabase.collection(USER).document(uId).collection(REFILLS).orderBy("currMileage", Query.Direction.DESCENDING).get().await()
                refillData?.let { emit(ItemModelState.Success(it.toObjects(HistoryItemModel::class.java))) }

            }else{
                emit(ItemModelState.Error( "Not logged"))
            }
        } catch (e: HttpException) {
            emit(ItemModelState.Error( e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(ItemModelState.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(ItemModelState.Error(e.localizedMessage ?: "Unknown exception"))
        }
    }

    override fun addDataRefillItem(
        currMileage: Float,
        fuelCost: Float,
        fuelAmount: Float,
        refillDate: String,
        notes: String
    ): Flow<ItemModelState> = flow {

        emit(ItemModelState.Loading)
        val firebaseUser = auth.currentUser
        try {
        if (firebaseUser != null){
            val uId = firebaseUser.uid
            val itemId = fireStoreDatabase.collection(USER).document(uId).collection(REFILLS).document().id
            val basicRefillModel = HistoryItemModel(itemId, currMileage, fuelAmount, fuelCost, refillDate, notes)
            fireStoreDatabase.collection(USER).document(uId).collection(REFILLS).document(itemId).set(basicRefillModel).await()
            val response = fireStoreDatabase.collection(USER).document(uId).collection(REFILLS).orderBy("currMileage", Query.Direction.DESCENDING).get().await()
            response?.let { emit(ItemModelState.Success(it.toObjects(HistoryItemModel::class.java))) }

        }else{
            emit(ItemModelState.Error( "Not logged"))
        }
    } catch (e: HttpException) {
        emit(ItemModelState.Error( e.localizedMessage ?: "Unknown Error"))
    } catch (e: IOException) {
        emit(ItemModelState.Error(e.localizedMessage ?: "Check Your Internet Connection"))
    } catch (e: Exception) {
        emit(ItemModelState.Error(e.localizedMessage ?: "Unknown exception"))
    }

    }

}