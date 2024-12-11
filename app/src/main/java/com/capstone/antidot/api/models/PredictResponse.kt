package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PredictResponse(

	@field:SerializedName("diagnosis")
	val diagnosis: Diagnosis,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
) : Parcelable

@Parcelize
data class Diagnosis(

	@field:SerializedName("disease")
	val disease: String,

	@field:SerializedName("diagnosisID")
	val diagnosisID: Int,

	@field:SerializedName("antibiotic_name")
	val antibioticName: String,

	@field:SerializedName("userID")
	val userID: Int,

	@field:SerializedName("disease_description")
	val diseaseDescription: String,

	@field:SerializedName("antibiotic_frequency_usage_per_day")
	val antibioticFrequencyUsagePerDay: String
) : Parcelable
