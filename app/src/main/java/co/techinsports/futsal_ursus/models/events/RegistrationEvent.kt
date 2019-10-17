package co.techinsports.futsal_ursus.models.events

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class RegistrationEvent (
    val success: Boolean,
    val reason: Int,
    val token: String?,
    val attended_groups: List<Int>
){
    class Deserializer: ResponseDeserializable<RegistrationEvent> {
        private val objType = object : TypeToken<RegistrationEvent?>() {}.type
        override fun deserialize(content: String): RegistrationEvent? =
            try { Gson().fromJson(content, objType) } catch (e: Exception) { null }
    }
}