package com.capstone.antidot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("HardcodedStringLiteral")
object RetrofitClient {
    class ApiService {

    }

    private const val BASE_URL = "https://backend-dot-united-planet-442804-p8.et.r.appspot.com/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}