package com.example.repnote.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSession(
    val id: String,
    val templateId: Long,
    val workoutTitle: String,
    val completedAtEpochMillis: Long,
    val timeZoneId: String,
    val exercises: List<CompletedExercise>
)