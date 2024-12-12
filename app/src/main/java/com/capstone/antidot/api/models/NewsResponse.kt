package com.capstone.antidot.api.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NewsResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("article")
	val article: List<ArticleItem>,

	@field:SerializedName("status")
	val status: String
) : Parcelable

@Parcelize
data class ArticleItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("articleID")
	val articleID: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("url")
	val url: String
) : Parcelable
