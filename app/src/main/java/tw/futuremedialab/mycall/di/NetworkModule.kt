package tw.futuremedialab.mycall.di

import tw.futuremedialab.mycall.data.network.ApiConfig
import tw.futuremedialab.mycall.data.network.AuthApiService
import tw.futuremedialab.mycall.data.network.FraudApiService
import tw.futuremedialab.mycall.data.network.PushApiService
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
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun providePushApiService(retrofit: Retrofit): PushApiService =
        retrofit.create(PushApiService::class.java)

    @Provides
    @Singleton
    fun provideFraudApiService(retrofit: Retrofit): FraudApiService =
        retrofit.create(FraudApiService::class.java)
}
