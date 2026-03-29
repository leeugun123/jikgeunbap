package com.example.jikgeunbap.app.di

import com.example.jikgeunbap.data.repository.RestaurantRepositoryImpl
import com.example.jikgeunbap.data.repository.WorkplaceRepositoryImpl
import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.data.source.remote.RemoteRestaurantDataSourceImpl
import com.example.jikgeunbap.data.source.remote.RestaurantApiService
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import com.example.jikgeunbap.domain.usecase.GetRandomLunchRestaurantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            // 안드로이드 에뮬레이터에서 로컬호스트에 접근할 때는 10.0.2.2 사용
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService =
        retrofit.create(RestaurantApiService::class.java)

    @Provides
    @Singleton
    fun provideWorkplaceRepository(
        apiService: RestaurantApiService
    ): WorkplaceRepository = WorkplaceRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideRemoteRestaurantDataSource(
        apiService: RestaurantApiService,
        workplaceRepository: WorkplaceRepository
    ): RemoteRestaurantDataSource =
        RemoteRestaurantDataSourceImpl(apiService, workplaceRepository)

    @Provides
    @Singleton
    fun provideRestaurantRepository(
        remoteRestaurantDataSource: RemoteRestaurantDataSource
    ): RestaurantRepository = RestaurantRepositoryImpl(remoteRestaurantDataSource)

    @Provides
    @Singleton
    fun provideGetRandomLunchRestaurantUseCase(repository: RestaurantRepository): GetRandomLunchRestaurantUseCase =
        GetRandomLunchRestaurantUseCase(repository)
}
