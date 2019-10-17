package co.techinsports.futsal_ursus.models.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Group (
    val id: Int,
    val name: String
){
    class Deserializer: ResponseDeserializable<List<Group>> {
        private val objType = object : TypeToken<List<Group>>() {}.type
        override fun deserialize(content: String): List<Group> =
            try { Gson().fromJson(content, objType) } catch (e: Exception) { emptyList() }
    }
}