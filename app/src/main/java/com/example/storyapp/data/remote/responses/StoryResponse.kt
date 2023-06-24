package com.example.storyapp.data.remote.responses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class StoryResponse(

    @field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
	val error: Boolean,

    @field:SerializedName("message")
	val message: String
)
@Parcelize
@Entity(tableName = "story")
data class ListStoryItem(
	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Float?,

	@field:SerializedName("lat")
	val lat: Float?
) : Parcelable
