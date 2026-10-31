package com.example.repnote.model

import kotlinx.serialization.Serializable

@Serializable
data class CompletedExercise(
    val exerciseId: Long,
    val exerciseName: String,
    val sets: List<CompletedSet>
)