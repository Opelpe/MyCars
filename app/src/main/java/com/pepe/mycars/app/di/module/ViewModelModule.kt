package com.pepe.mycars.app.di.module

import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetUserDataUseCase
import com.pepe.mycars.app.viewmodel.HistoryViewViewModel
import com.pepe.mycars.app.viewmodel.MainViewViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideMainViewViewModel(
        userRepository: UserRepository,
        getUserDataUseCase: GetUserDataUseCase,

        ): MainViewViewModel {
        return MainViewViewModel(userRepository, getUserDataUseCase)
    }

    @Provides
    @Singleton
    fun provideHistoryItemViewViewModel(
        deleteItemUseCase: DeleteItemUseCase,
        getUserDataUseCase: GetUserDataUseCase,

        ): HistoryViewViewModel {
        return HistoryViewViewModel(getUserDataUseCase, deleteItemUseCase)
    }

}