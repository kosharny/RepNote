package com.example.repnote.model

import kotlinx.serialization.Serializable

@Serializable
data class CompletedSet(
    val weightKg: Double,
    val reps: Int
)