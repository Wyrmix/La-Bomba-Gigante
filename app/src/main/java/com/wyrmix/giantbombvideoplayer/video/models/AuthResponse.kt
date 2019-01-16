package com.wyrmix.giantbombvideoplayer.video.models

import com.google.gson.annotations.SerializedName

/**
 * Created by kylea
 *
 * 10/15/2018 at 7:17 PM
 */
data class AuthResponse(@SerializedName("api_key") var apiKey: String, @SerializedName("expires") var expires: Long)