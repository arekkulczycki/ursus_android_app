package com.example.futsal_ursus.models.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.util.*

data class Event (
    val id: Int,
    val type_name: String,
    val name: String,
    val start_date: Date,
    val end_date: Date,
    val address: String,
    val present: Boolean?,
    val participants_present: Int,
    val participants_max: Int
){
    class Deserializer: ResponseDeserializable<List<Event>> {
        private val objType = object : TypeToken<List<Event>>() {}.type
        override fun deserialize(content: String): List<Event> =
            try { Gson().fromJson(content, objType) } catch (e: Exception) { emptyList() }
    }
}