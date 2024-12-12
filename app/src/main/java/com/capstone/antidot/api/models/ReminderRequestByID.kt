package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ReminderRequestByID(

	@field:SerializedName("reminderFrequency")
	val reminderFrequency: String,

	@field:SerializedName("customAntibioticName")
	val customAntibioticName: String,

	@field:SerializedName("reminderTimes")
	val reminderTimes: List<String>
) : Parcelable
