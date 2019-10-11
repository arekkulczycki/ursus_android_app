package com.example.futsal_ursus.models

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context){
    val PREFS_FILENAME = "preferences"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var testVar: String?
        get() = prefs.getString("testVar", null)
        set(value) = prefs.edit().putString("testVar", value).apply()

    var login_token: String?
        get() = prefs.getString("login_token", null)
        set(value) = prefs.edit().putString("login_token", value).apply()
}