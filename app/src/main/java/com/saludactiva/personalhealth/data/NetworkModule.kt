package com.saludactiva.personalhealth.data

import android.content.Context
import com.saludactiva.personalhealth.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    fun createApi(context: Context): ApiService {
        val sessions = SessionManager(context.applicationContext)
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().apply {
                sessions.token?.let { token -> header("Authorization", "Bearer $token") }
            }.build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
