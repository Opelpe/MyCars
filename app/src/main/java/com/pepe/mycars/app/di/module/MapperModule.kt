package com.pepe.mycars.app.di.module

import android.content.Context
import com.pepe.mycars.app.data.mapper.ErrorMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    @Provides
    @Singleton
    fun provideErrorMapper(
        @ApplicationContext appContext: Context,
    ): ErrorMapper {
        return ErrorMapper(appContext)
    }
}
