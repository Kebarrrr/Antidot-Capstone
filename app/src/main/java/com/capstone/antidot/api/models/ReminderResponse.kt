package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ReminderResponse(

	@field:SerializedName("reminders")
	val reminders: List<RemindersItem>,

	@field:SerializedName("userID")
	val userID: Int,

	@field:SerializedName("status")
	val status: String
) : Parcelable

@Parcelize
data class RemindersItem(

	@field:SerializedName("reminderFrequency")
	val reminderFrequency: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("deleted")
	val deleted: Boolean,

	@field:SerializedName("reminderID")
	val reminderID: Int,

	@field:SerializedName("customAntibioticName")
	val customAntibioticName: String,

	@field:SerializedName("userID")
	val userID: Int,

	@field:SerializedName("reminderTimes")
	val reminderTimes: List<String>,

	@field:SerializedName("antibioticID")
	val antibioticID: Integer?,

	@field:SerializedName("updatedAt")
	val updatedAt: String
) : Parcelable
