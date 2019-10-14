package com.example.futsal_ursus.models.events

class RegistrationEvent (
    val success: Boolean,
    val reason: Int,
    val token: String?
)