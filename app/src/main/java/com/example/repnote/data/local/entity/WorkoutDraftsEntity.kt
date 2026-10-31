package com.example.repnote.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "workout_drafts")
data class WorkoutDraftsEntity(
    @PrimaryKey
    val id: Int = 1,
    val json: String
)