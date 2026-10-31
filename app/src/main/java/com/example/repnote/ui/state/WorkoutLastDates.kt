package com.example.repnote.ui.state

import com.example.repnote.model.WorkoutSessionSummary
import com.example.repnote.model.WorkoutTemplate

fun withLastWorkoutDates(workouts: List<WorkoutTemplate>, sessions: List<WorkoutSessionSummary>): List<WorkoutTemplate> =
    workouts.map { workout ->
        val latest = sessions.filter { it.templateId == workout.id }.maxByOrNull { it.completedAtEpochMillis }
        workout.copy(lastCompletedAtEpochMillis = latest?.completedAtEpochMillis,
            lastTimeZoneId = latest?.timeZoneId ?: "UTC")
    }
