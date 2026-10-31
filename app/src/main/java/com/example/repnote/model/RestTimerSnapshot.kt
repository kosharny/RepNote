package com.example.repnote.model

import kotlinx.serialization.Serializable

@Serializable
data class RestTimerSnapshot(
    val workoutId: Long? = null,
    val durationSeconds: Int = 90,
    val remainingSeconds: Int = 90,
    val deadlineElapsedMillis: Long? = null,
    val bootCount: Int = -1
)
