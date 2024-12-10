package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DiagnosisRequest(

	@field:SerializedName("symptoms")
	val symptoms: List<String>,

	val isUserAdded: Boolean = false
) : Parcelable
