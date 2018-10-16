package com.wyrmix.giantbombvideoplayer.video.models

import com.google.gson.annotations.SerializedName

data class Key (@SerializedName("apiKey") var apiKey: String, @SerializedName("expires") var expires: Long)
