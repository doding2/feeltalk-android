package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.api.GoogleAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            .apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
    }

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
//            .connectTimeout(5, TimeUnit.SECONDS)
//            .readTimeout(5, TimeUnit.SECONDS)
//            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named("GOOGLE_AUTH")
    fun providesGoogleAuthRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.GOOGLE_AUTH_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("CLONECT")
    fun providesClonectRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.CLONECT_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleAuthService(@Named("GOOGLE_AUTH") retrofit: Retrofit): GoogleAuthService {
        return retrofit.create(GoogleAuthService::class.java)
    }

    @Singleton
    @Provides
    fun providesClonectService(@Named("CLONECT") retrofit: Retrofit): ClonectService {
        return retrofit.create(ClonectService::class.java)
    }

}