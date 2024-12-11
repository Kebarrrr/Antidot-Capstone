package com.capstone.antidot.api

import com.capstone.antidot.api.models.AntibioticDetailResponse
import com.capstone.antidot.api.models.AntibioticsResponse
import com.capstone.antidot.api.models.ApiResponse
import com.capstone.antidot.api.models.PredictResponse
import com.capstone.antidot.api.models.RegisterRequest
import com.capstone.antidot.api.models.SymptomsRequest
import com.capstone.antidot.api.models.UserRequest
import com.capstone.antidot.api.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserRequest): ApiResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("diagnosis/predict")
    suspend fun diagnosis(@Body symptomsRequest: SymptomsRequest): PredictResponse

    @GET("diagnosis/status/{id}")
    suspend fun getDiagnosisStatus(@Path("id") diagnosisID: Int): PredictResponse

    @GET("profile")
    suspend fun userProfile(): UserResponse

    @GET("/antibiotics")
    suspend fun getAntibiotics(): AntibioticsResponse

    @GET("antibiotics/{id}")
    suspend fun getDetailAntibiotics(@Path("id") id: String): AntibioticDetailResponse
}