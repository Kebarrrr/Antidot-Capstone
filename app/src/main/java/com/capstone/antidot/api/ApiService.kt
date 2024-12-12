package com.capstone.antidot.api

import com.capstone.antidot.api.models.AntibioticDetailResponse
import com.capstone.antidot.api.models.AntibioticsItem
import com.capstone.antidot.api.models.AntibioticsResponse
import com.capstone.antidot.api.models.ApiResponse
import com.capstone.antidot.api.models.NewsResponse
import com.capstone.antidot.api.models.PredictResponse
import com.capstone.antidot.api.models.RegisterRequest
import com.capstone.antidot.api.models.ReminderRequest
import com.capstone.antidot.api.models.ReminderRequestByID
import com.capstone.antidot.api.models.ReminderResponse
import com.capstone.antidot.api.models.SymptomsRequest
import com.capstone.antidot.api.models.SymptomsResponse
import com.capstone.antidot.api.models.UserRequest
import com.capstone.antidot.api.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserRequest): ApiResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("reminders/create")
    suspend fun postReminder(@Body request: ReminderRequest) : ApiResponse

    @POST("reminders/create")
    suspend fun postReminderByID(@Body request: ReminderRequestByID) : ApiResponse


    @POST("diagnosis/predict")
    suspend fun sendSymptoms(@Body symptomsRequest: SymptomsRequest): PredictResponse

    @GET("diagnosis/{id}")
    suspend fun getPredict(@Path("id") id: String): PredictResponse

    @GET("diagnosis/symptoms")
    suspend fun getSymptoms(): SymptomsResponse

    @GET("profile")
    suspend fun userProfile(): UserResponse

    @GET("reminders/")
    suspend fun getReminder(): ReminderResponse

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(@Path("id") reminderId: String): Response<Void>

    @GET("/antibiotics")
    suspend fun getAntibiotics(): AntibioticsResponse

    @GET("antibiotics/{id}")
    suspend fun getDetailAntibiotics(@Path("id") id: String): AntibioticDetailResponse

    @GET("articles")
    suspend fun getArticle(): NewsResponse
}