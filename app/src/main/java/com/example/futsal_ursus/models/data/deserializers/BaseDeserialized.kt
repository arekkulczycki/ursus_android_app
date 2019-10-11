package com.example.futsal_ursus.models.data.deserializers

import com.example.futsal_ursus.models.data.Participant
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface BaseDeserialized<T> {
    class Deserializer: ResponseDeserializable<List<Participant>> {
        private val objType = object : TypeToken<List<Participant>>() {}.type
        override fun deserialize(content: String): List<Participant>? = Gson().fromJson(content, objType)
    }
}