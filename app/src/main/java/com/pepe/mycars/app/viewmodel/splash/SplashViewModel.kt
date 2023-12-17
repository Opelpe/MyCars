//package com.pepe.mycars.app.viewmodel.splash
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.pepe.mycars.app.data.domain.repository.UserRepository
//import com.pepe.mycars.app.ui.view.splash.SplashViewModelState
//import dagger.Lazy
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class SplashViewModel @Inject constructor(private val repository: Lazy<UserRepository>) :
//    ViewModel() {
//
//
//    val viewState = MutableLiveData(SplashViewModelState())
////     private val compositeDisposable = CompositeDisposable()
//
//    init {
//        isUserLogged()
//    }
//
//
////     fun get(refresh: Boolean = false) =
////          compositeDisposable.add(userUseCase.get(refresh)
////               .doOnSubscribe { posts.setLoading() }
////               .subscribeOn(Schedulers.io())
////               .map { it.mapToPresentation() }
////               .subscribe({ posts.setSuccess(it) }, { posts.setError(it.message) })
////          )
//
//    private fun isUserLogged() {
//
//        val isLogged = repository.get().isUserLogged()
//        if (isLogged) {
//            viewModelScope.launch {
//                viewState.value = viewState.value?.copy(showToastMessage = true, toastMessage = "Zalogowano",loggedIn = isLogged)
//            }
//        } else {
//            viewModelScope.launch {
//                viewState.value = viewState.value?.copy(showToastMessage = true, toastMessage = "nie zalogowano", loggedIn = isLogged)
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//
//    }
//    //click(){
//// 1. dane z usecsa
//    //  2 robi
//// 3,                 viewState.value = viewState.value?.copy(showToastMessage = true, test = "pierwszy")
//// }
//
//}