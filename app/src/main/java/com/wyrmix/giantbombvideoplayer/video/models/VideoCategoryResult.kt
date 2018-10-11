package com.wyrmix.giantbombvideoplayer.video.models

import com.google.gson.annotations.SerializedName
import com.wyrmix.giantbombvideoplayer.video.database.VideoCategory

data class VideoCategoryResult(
        @SerializedName("error") val error: String = "",
        @SerializedName("limit") val limit: Int = -1,
        @SerializedName("offset") val offset: Int = -1,
        @SerializedName("number_of_page_results") val numPages: Int = -1,
        @SerializedName("number_of_total_results") val numResults: Int = -1,
        @SerializedName("status_code") val statusCode: Int = -1,
        @SerializedName("results") val results: List<VideoCategory> = emptyList())
