package com.example.futsal_ursus.models.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

data class Participant (
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String
){
    class Deserializer: ResponseDeserializable<List<Participant>> {
        private val objType = object : TypeToken<List<Participant>>() {}.type
        override fun deserialize(content: String): List<Participant> =
            try { Gson().fromJson(content, objType) } catch (e: Exception) { emptyList() }
    }
}