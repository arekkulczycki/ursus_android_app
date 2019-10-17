package co.techinsports.futsal_ursus.models.data.deserializers

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BoolDeserializer: ResponseDeserializable<Pair<String, Boolean>> {
    private val objType = object : TypeToken<Pair<String, Boolean>>() {}.type
    override fun deserialize(content: String): Pair<String, Boolean>? =
        Gson().fromJson(content, objType)
}