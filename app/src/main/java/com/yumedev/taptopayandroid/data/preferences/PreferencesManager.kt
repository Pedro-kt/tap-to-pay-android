package com.yumedev.taptopayandroid.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "tap_to_pay_preferences"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_THEME_MODE = "theme_mode"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // Sound preferences
    var isSoundEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_SOUND_ENABLED, value) }

    // Theme preferences
    var themeMode: String
        get() = sharedPreferences.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = sharedPreferences.edit { putString(KEY_THEME_MODE, value) }
}
