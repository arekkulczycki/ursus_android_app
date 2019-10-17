package co.techinsports.futsal_ursus.models.events

class ServerErrorEvent (
    val status_code: Int,
    val message: String?,
    val request_flag: String? = null
)