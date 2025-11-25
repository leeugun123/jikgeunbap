package com.example.jikgeunbap.app.di

import com.example.jikgeunbap.app.data.repository.RestaurantRepositoryImpl
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository
import com.example.jikgeunbap.app.domain.usecase.GetRestaurantsUseCase
import com.example.jikgeunbap.app.data.repository.AiLunchRecommendRepositoryImpl
import com.example.jikgeunbap.app.domain.repository.AiLunchRecommendRepository
import com.example.jikgeunbap.app.domain.usecase.GetAiLunchRecommendUseCase
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
    fun provideGetRestaurantsUseCase(repository: RestaurantRepository): GetRestaurantsUseCase =
        GetRestaurantsUseCase(repository)

    @Provides
    @Singleton
    fun provideAiLunchRecommendRepository(): AiLunchRecommendRepository = AiLunchRecommendRepositoryImpl()

    @Provides
    @Singleton
    fun provideGetAiLunchRecommendUseCase(aiRepo: AiLunchRecommendRepository): GetAiLunchRecommendUseCase =
        GetAiLunchRecommendUseCase(aiRepo)
}
