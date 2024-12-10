package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserResponse(
    val user: User,
    val status: String
) : Parcelable

@Parcelize
data class User(
    val profilePicture: String,
    val fullName: String,
    val userID: Int,
    val birthDate: String,
    val age: Int,
    val email: String
):Parcelable