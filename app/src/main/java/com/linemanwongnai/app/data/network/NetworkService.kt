package com.linemanwongnai.app.data.network

import com.linemanwongnai.app.utils.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkService {
    private const val BASE_URL = "https://api.coinranking.com"

//    private val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {

        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun headerInterceptor(): Interceptor {
        return Interceptor {
            val request = it.request().newBuilder()
            request.addHeader("x-access-token", Utils.API_KEY)
            val actualRequest = request.build()
            it.proceed(actualRequest)
        }
    }

//    private val client = OkHttpClient.Builder().apply {
//
//        addInterceptor(logging)
//            .addInterceptor()
//            .readTimeout(5, TimeUnit.SECONDS)
//            .writeTimeout(5, TimeUnit.SECONDS)
//            .connectTimeout(5, TimeUnit.SECONDS)
//    }.build()

    @Provides
    @Singleton
    fun getOkHttpClient(
        interceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
        return httpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideCoinApi(client: OkHttpClient): CoinApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CoinApi::class.java)
    }
}

