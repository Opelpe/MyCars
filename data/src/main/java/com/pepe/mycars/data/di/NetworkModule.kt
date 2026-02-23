package com.pepe.mycars.data.di

import com.pepe.mycars.data.network.manager.NetworkManager
import com.pepe.mycars.domain.manager.INetworkManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    abstract fun bindNetworkManager(networkManager: NetworkManager): INetworkManager
}
