package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AntibioticsResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("antibiotics")
	val antibiotics: List<AntibioticsItem>,

	@field:SerializedName("status")
	val status: String
) : Parcelable

@Parcelize
data class AntibioticsItem(

	@field:SerializedName("antibiotics_dosage")
	val antibioticsDosage: String,

	@field:SerializedName("disease")
	val disease: String,

	@field:SerializedName("antibiotics_usage")
	val antibioticsUsage: String,

	@field:SerializedName("antibiotic_total_days_of_usage")
	val antibioticTotalDaysOfUsage: String,

	@field:SerializedName("antibiotics_description")
	val antibioticsDescription: String,

	@field:SerializedName("antibiotic_image")
	val antibioticImage: String,

	@field:SerializedName("antibiotics_name")
	val antibioticsName: String,

	@field:SerializedName("antibiotic_frequency_usage_per_day")
	val antibioticFrequencyUsagePerDay: String,

	@field:SerializedName("others")
	val others: String,

	@field:SerializedName("antibioticID")
	val antibioticID: Int
) : Parcelable

@Parcelize
data class AntibioticDetailResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("antibioticByID")
	val antibioticByID: AntibioticsItem,

	@field:SerializedName("status")
	val status: String
) : Parcelable
