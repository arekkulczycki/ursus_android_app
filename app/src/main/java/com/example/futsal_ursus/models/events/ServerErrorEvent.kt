package com.example.futsal_ursus.models.events

class ServerErrorEvent (
    val status_code: Int,
    val message: String?
)