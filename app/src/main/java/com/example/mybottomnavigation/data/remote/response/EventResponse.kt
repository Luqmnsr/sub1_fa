package com.example.mybottomnavigation.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(

    @field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem>,
)

data class ListEventsItem(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("summary")
	val summary: String = "No description available",

	@field:SerializedName("imageLogo")
	val imageLogo: String = "",

	@field:SerializedName("beginTime")
	val beginTime: String = ""
)
