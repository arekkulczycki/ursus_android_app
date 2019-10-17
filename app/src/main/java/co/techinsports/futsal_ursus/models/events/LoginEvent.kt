package co.techinsports.futsal_ursus.models.events

import co.techinsports.futsal_ursus.models.data.Event
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class LoginEvent (
    val success: Boolean,
    val token: String?,
    val attended_groups: List<Int>
){
    class Deserializer: ResponseDeserializable<LoginEvent> {
        private val objType = object : TypeToken<LoginEvent?>() {}.type
        override fun deserialize(content: String): LoginEvent? =
            try { Gson().fromJson(content, objType) } catch (e: Exception) { null }
    }
}