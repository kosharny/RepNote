package com.example.repnote.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey

@Entity(
    tableName = "session_sets",
    primaryKeys = ["sessionId", "exerciseId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = SessionExerciseEntity::class,
            parentColumns = ["sessionId", "exerciseId"],
            childColumns = ["sessionId", "exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SessionSetEntity(
    val sessionId: String,
    val exerciseId: Long,
    val position: Int,
    val weightKg: Double,
    val reps: Int
)