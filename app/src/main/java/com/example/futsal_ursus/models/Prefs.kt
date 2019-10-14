package com.example.futsal_ursus.models

import android.content.Context
import android.content.SharedPreferences
import com.example.futsal_ursus.models.data.Event

class Prefs (context: Context) {
    val PREFS_FILENAME = "preferences"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var login_token: String?
        get() = prefs.getString("login_token", null)
        set(value) = prefs.edit().putString("login_token", value).apply()

    var next_training_address: String?
        get() = prefs.getString("next_training_address", null)
        set(value) = prefs.edit().putString("login_token", value).apply()

    var next_training_datetime: String?
        get() = prefs.getString("next_training_datetime", null)
        set(value) = prefs.edit().putString("next_training_datetime", value).apply()

    var next_match_address: String?
        get() = prefs.getString("next_match_address", null)
        set(value) = prefs.edit().putString("next_match_address", value).apply()

    var next_match_datetime: String?
        get() = prefs.getString("next_match_datetime", null)
        set(value) = prefs.edit().putString("next_match_datetime", value).apply()

    var present_next_training: Boolean?
        get() = if (prefs.getInt("present_next_training", 0) == 0) null
                else prefs.getInt("present_next_training", 0) == 1
        set(value) = prefs.edit().putInt("present_next_training", if (value == null) 0 else if (value) 1 else 2).apply()

    var present_next_match: Boolean?
        get() = if (prefs.getInt("present_next_match", 0) == 0) null
                else prefs.getInt("present_next_match", 0) == 1
        set(value) = prefs.edit().putInt("present_next_match", if (value == null) 0 else if (value) 1 else 2).apply()
}