package com.capstone.antidot.api

import android.content.Context
import android.util.Log
import com.capstone.antidot.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("HardcodedStringLiteral")
object RetrofitClient {

    private const val BASE_URL = "https://backend-dot-united-planet-442804-p8.et.r.appspot.com/"

    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): ApiService {
        val token = SessionManager(context).getToken() // Ambil token dari SessionManager
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = SessionManager(context).getToken()
                Log.d("RetrofitClient", "Token used in request: $token") // Debug log
                val request = chain.request().newBuilder()
                    .apply {
                        if (!token.isNullOrEmpty()) {
                            addHeader("Authorization", "Bearer $token")
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}