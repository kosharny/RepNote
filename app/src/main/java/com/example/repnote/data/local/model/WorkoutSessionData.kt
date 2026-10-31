package com.example.repnote.data.local.model

import com.example.repnote.data.local.entity.SessionExerciseEntity
import com.example.repnote.data.local.entity.SessionSetEntity
import com.example.repnote.data.local.entity.WorkoutSessionEntity

data class WorkoutSessionData(
    val session: WorkoutSessionEntity,
    val exercises: List<SessionExerciseEntity>,
    val sets: List<SessionSetEntity>
)