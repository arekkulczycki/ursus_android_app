package co.techinsports.futsal_ursus.models.data.deserializers

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapDeserializer: ResponseDeserializable<Map<String, String>> {
    private val objType = object : TypeToken<Map<String, String>>() {}.type
    override fun deserialize(content: String): Map<String, String>? =
        Gson().fromJson(content, objType)
}