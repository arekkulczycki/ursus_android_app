package com.example.futsal_ursus

@Suppress("ConstantConditionIf")
class AppSettings {
    companion object {
        const val PREFERENCE_FILE = "preferences"
        const val server = "dev"
        private val serverUrl: String
            get() = when {
                server == "local" -> "http://10.0.2.2:8000"
                server == "dev" -> "https://ursus-backend.herokuapp.com"
                else -> "http://10.0.2.2:8000"
            }
        fun getUrl(url: String): String {
            return String.format("%s%s", serverUrl, url)
        }
    }
}