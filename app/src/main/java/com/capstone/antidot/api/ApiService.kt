package com.capstone.antidot.api

import com.capstone.antidot.api.models.AntibioticDetailResponse
import com.capstone.antidot.api.models.AntibioticsItem
import com.capstone.antidot.api.models.AntibioticsResponse
import com.capstone.antidot.api.models.ApiResponse
import com.capstone.antidot.api.models.RegisterRequest
import com.capstone.antidot.api.models.UserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserRequest): ApiResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @GET("profile")
    suspend fun userProfile(@Header("Authorization") token: String): ApiResponse

    @GET("/antibiotics")
    suspend fun getAntibiotics(): AntibioticsResponse

    @GET("antibiotics/{id}")
    suspend fun getDetailAntibiotics(@Path("id") id: String): AntibioticDetailResponse
}