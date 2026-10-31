package com.example.repnote.model

data class PreviousExerciseResult(
    val sessionId: String,
    val completedAtEpochMillis: Long,
    val timeZoneId: String,
    val exercise: CompletedExercise
)