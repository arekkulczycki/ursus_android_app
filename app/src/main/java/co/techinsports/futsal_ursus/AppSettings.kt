package co.techinsports.futsal_ursus

@Suppress("ConstantConditionIf")
class AppSettings {
    companion object {
        const val PREFERENCE_FILE = "preferences"
        const val server = "dev"
        private val serverUrl: String
            get() = when {
                server == "local" -> "http://10.0.2.2:8000"
                server == "local2" -> "http://192.168.1.11:8000"
                server == "office" -> "http://10.45.45.115:8800"
                server == "dev" -> "https://ursus-backend.herokuapp.com"
                server == "production" -> "https://techinsports.com.pl"
                else -> "http://10.0.2.2:8000"
            }
        fun getUrl(url: String): String {
            return String.format("%s%s",
                serverUrl, url)
        }
        const val MINIMUM_PARTICIPANTS = 10
        const val HOURS_BEFORE_EVENT = 8
    }
}