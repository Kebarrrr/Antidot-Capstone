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

	@field:SerializedName("antibioticImage")
	val antibioticImage: String,

	@field:SerializedName("antibioticName")
	val antibioticName: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("antibioticID")
	val antibioticID: Int
) : Parcelable
