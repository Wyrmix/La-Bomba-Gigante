package com.wyrmix.giantbombvideoplayer.auth

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.wyrmix.giantbombvideoplayer.video.network.GiantbombApiClient
import timber.log.Timber

const val API_KEY = "API_KEY"
const val FAILED_TO_RETRIEVE_API_KEY = "Failed to retrieve API key"
const val GETTING_YOUR_API_KEY = "Getting your API key..."
const val GOT_YOUR_API_KEY = "Success! We downloaded your API key."

class AuthenticationViewModel(app: Application, private val sharedPrefs: SharedPreferences, private val apiClient: GiantbombApiClient) : AndroidViewModel(app) {
    suspend fun authenticate(authCode: String): Boolean {
        var result = false

        try {
            val data = apiClient.getApiKey(authCode, "json").await()
            Timber.d("Api key [$data]")
            sharedPrefs.edit().putString(API_KEY, data.key.apiKey).apply()
            result = true
        } catch (t: Throwable) {
            Timber.e(t, "error getting API key")
        }

        return result
    }

    fun getApiKey(): String = sharedPrefs.getString(API_KEY, "No Saved API Key") ?: ""
}