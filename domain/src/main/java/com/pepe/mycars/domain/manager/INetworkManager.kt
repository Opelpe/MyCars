package com.pepe.mycars.domain.manager

import kotlinx.coroutines.flow.Flow

interface INetworkManager {
    val isConnected: Flow<Boolean>
}
