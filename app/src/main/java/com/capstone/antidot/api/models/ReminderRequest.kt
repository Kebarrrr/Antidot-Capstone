package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ReminderRequest(

	@field:SerializedName("reminderFrequency")
	val reminderFrequency: String,

	@field:SerializedName("reminderTimes")
	val reminderTimes: List<String>,

	@field:SerializedName("antibioticID")
	val antibioticID: Int
) : Parcelable
