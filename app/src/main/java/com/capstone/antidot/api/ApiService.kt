package com.capstone.antidot.api

import com.capstone.antidot.api.models.ApiResponse
import com.capstone.antidot.api.models.RegisterRequest
import com.capstone.antidot.api.models.UserRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserRequest): ApiResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse
}