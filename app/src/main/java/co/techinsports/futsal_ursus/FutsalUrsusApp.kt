package co.techinsports.futsal_ursus

import android.app.Application
import co.techinsports.futsal_ursus.models.Prefs

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