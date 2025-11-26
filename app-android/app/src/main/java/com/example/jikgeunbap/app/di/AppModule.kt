package com.example.jikgeunbap.app.di

import com.example.jikgeunbap.app.data.repository.RestaurantRepositoryImpl
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository
import com.example.jikgeunbap.app.domain.usecase.GetRandomLunchRestaurantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRestaurantRepository(): RestaurantRepository = RestaurantRepositoryImpl()

    @Provides
    @Singleton
    fun provideGetRandomLunchRestaurantUseCase(repository: RestaurantRepository): GetRandomLunchRestaurantUseCase =
        GetRandomLunchRestaurantUseCase(repository)
}
