package co.techinsports.futsal_ursus.models.data

data class Credentials (
    val username: String,
    val password: String,
    val group_id: Int? = null
)