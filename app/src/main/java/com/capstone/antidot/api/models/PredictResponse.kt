package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

enum class AntibioticFrequency(val description: String) {
	@SerializedName("1x sehari")
	ONCE_A_DAY("1x sehari"),
	@SerializedName("2x sehari")
	TWICE_A_DAY("2x sehari"),
	@SerializedName("3x sehari")
	THRICE_A_DAY("3x sehari");

	companion object {
		fun fromString(value: String): AntibioticFrequency? {
			return entries.find { it.description == value }
		}
	}
}


// Data class untuk response Prediksi
@Parcelize
data class PredictResponse(
	@field:SerializedName("diagnosis")
	val diagnosis: Diagnosis,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
) : Parcelable

// Data class untuk Diagnosis
@Parcelize
data class Diagnosis(
	@field:SerializedName("userID")
	val userID: Int,

	@field:SerializedName("diagnosisID")
	val diagnosisID: Int,

	@field:SerializedName("disease")
	val disease: String,

	@field:SerializedName("probability")
	val probability: String,

	@field:SerializedName("disease_description")
	val diseaseDescription: String,

	@field:SerializedName("antibiotic_name")
	val antibioticName: String,

	@field:SerializedName("antibiotics_usage")
	val antibioticsUsage: String,

	@field:SerializedName("antibiotics_description")
	val antibioticsDescription: String,

	@field:SerializedName("antibiotics_dosage")
	val antibioticsDosage: String,

	@field:SerializedName("antibiotic_fraquency_usage_per_day")
	val antibioticFrequencyUsagePerDay: AntibioticFrequency?,

	@field:SerializedName("antibiotic_total_days_of_usage")
	val antibioticTotalDaysOfUsage: String,

	@field:SerializedName("others")
	val others: String,

	@field:SerializedName("antibiotic_image")
	val antibioticImage: String
) : Parcelable

// Data class untuk response gejala
@Parcelize
data class SymptomsResponse(
	val message: String,
	val status: String,
	val diagnosis: Diagnosis,
	val symptoms: List<Symptoms> // Annotate with @RawValue
) : Parcelable

// Data class untuk request gejala
@Parcelize
data class SymptomsRequest(
	val symptoms: List<String>
) : Parcelable

// Data class untuk gejala
@Parcelize
data class Symptoms(
	var symptomName: String,
	var isSelected: Boolean = false
) : Parcelable
