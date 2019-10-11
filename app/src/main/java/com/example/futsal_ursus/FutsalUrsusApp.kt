package com.example.futsal_ursus

import android.app.Application
import com.example.futsal_ursus.models.Prefs

val prefs: Prefs by lazy {
    FutsalUrsusApp.prefs!!
}

class FutsalUrsusApp : Application() {
    companion object {
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}