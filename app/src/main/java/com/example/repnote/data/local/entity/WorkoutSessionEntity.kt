package com.example.repnote.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey
    val id: String,
    val templateId: Long,
    val workoutTitle: String,
    val completedAtEpochMillis: Long,
    val timeZoneId: String,
)