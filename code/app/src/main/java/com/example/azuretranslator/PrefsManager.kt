package com.example.azuretranslator

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("azure_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SPEECH_KEY = "speech_key"
        private const val KEY_REGION = "region"
        
        // Default values from the original script
        const val DEFAULT_KEY = ""
        const val DEFAULT_REGION = "eastus"
    }

    var speechKey: String
        get() = prefs.getString(KEY_SPEECH_KEY, DEFAULT_KEY) ?: DEFAULT_KEY
        set(value) = prefs.edit().putString(KEY_SPEECH_KEY, value).apply()

    var region: String
        get() = prefs.getString(KEY_REGION, DEFAULT_REGION) ?: DEFAULT_REGION
        set(value) = prefs.edit().putString(KEY_REGION, value).apply()
}
