package com.example.repnote.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey

@Entity(
    tableName = "session_exercises",
    primaryKeys = ["sessionId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SessionExerciseEntity(
    val sessionId: String,
    val exerciseId: Long,
    val exerciseName: String,
    val position: Int
)