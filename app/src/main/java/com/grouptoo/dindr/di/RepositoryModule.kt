package com.grouptoo.dindr.di

import com.grouptoo.dindr.data.repository.AuthRepository
import com.grouptoo.dindr.data.repository.AuthRepositoryReal
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import com.grouptoo.dindr.data.repository.PlacesRepository
import com.grouptoo.dindr.data.repository.PlacesRepositoryFake
import com.grouptoo.dindr.data.repository.PlacesRepositoryReal
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDataRepository(
        dataRepositoryReal: DataRepositoryReal
    ) : DataRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryReal: AuthRepositoryReal
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindPlacesRepository(
        placesRepositoryReal: PlacesRepositoryReal
    ) : PlacesRepository

}