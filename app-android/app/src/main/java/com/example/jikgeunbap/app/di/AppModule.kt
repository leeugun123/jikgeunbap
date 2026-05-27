package com.example.jikgeunbap.app.di

import android.content.Context
import com.example.jikgeunbap.app.BuildConfig
import com.example.jikgeunbap.data.repository.FeedbackRepositoryImpl
import com.example.jikgeunbap.data.repository.KakaoLocalRepositoryImpl
import com.example.jikgeunbap.data.repository.OnboardingRepositoryImpl
import com.example.jikgeunbap.data.repository.RestaurantRepositoryImpl
import com.example.jikgeunbap.data.repository.WorkplaceRepositoryImpl
import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.data.source.remote.KakaoLocalApiService
import com.example.jikgeunbap.data.source.remote.RemoteRestaurantDataSourceImpl
import com.example.jikgeunbap.data.source.remote.RestaurantApiService
import com.example.jikgeunbap.domain.repository.FeedbackRepository
import com.example.jikgeunbap.domain.repository.KakaoLocalRepository
import com.example.jikgeunbap.domain.repository.OnboardingRepository
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── 백엔드 API ────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    @Named("logging")
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
    fun provideRetrofit(@Named("logging") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            // 안드로이드 에뮬레이터에서 로컬호스트 접근 시 10.0.2.2 사용
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService =
        retrofit.create(RestaurantApiService::class.java)

    // ── Kakao Local API ───────────────────────────────────────────────────────

    @Provides
    @Singleton
    @Named("kakao_local")
    fun provideKakaoOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                .build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("kakao_local")
    fun provideKakaoLocalRetrofit(@Named("kakao_local") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideKakaoLocalApiService(@Named("kakao_local") retrofit: Retrofit): KakaoLocalApiService =
        retrofit.create(KakaoLocalApiService::class.java)

    @Provides
    @Singleton
    fun provideKakaoLocalRepository(
        apiService: KakaoLocalApiService
    ): KakaoLocalRepository = KakaoLocalRepositoryImpl(apiService)

    // ── Repository ────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        @ApplicationContext context: Context
    ): OnboardingRepository = OnboardingRepositoryImpl(context)

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
    fun provideFeedbackRepository(
        apiService: RestaurantApiService
    ): FeedbackRepository = FeedbackRepositoryImpl(apiService)
}
