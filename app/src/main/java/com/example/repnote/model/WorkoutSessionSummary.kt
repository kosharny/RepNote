package com.example.repnote.model

data class WorkoutSessionSummary(
    val id: String,
    val workoutTitle: String,
    val completedAtEpochMillis: Long,
    val timeZoneId: String,
    val templateId: Long,
)