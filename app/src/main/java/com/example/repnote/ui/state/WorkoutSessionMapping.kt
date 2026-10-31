package com.example.repnote.ui.state

import com.example.repnote.model.WorkoutSession
import com.example.repnote.model.WorkoutTemplate

fun toWorkoutSession(
    workout: WorkoutTemplate,
    draft: WorkoutDraftUiState?,
    sessionId: String,
    completedAtEpochMillis: Long,
    timeZoneId: String
): WorkoutSession? {
    if ( draft == null || draft.workoutId != workout.id) return null
    val completedExercises = workout.exercises.mapNotNull { exercise ->
        val entries = draft.entriesByExercise[exercise.id].orEmpty()
        toCompletedExercise(exercise, entries)
    }
    if (completedExercises.isEmpty()) return null
    return WorkoutSession(
        id = sessionId,
        templateId = workout.id,
        workoutTitle = workout.title,
        completedAtEpochMillis = completedAtEpochMillis,
        timeZoneId = timeZoneId,
        exercises = completedExercises
    )
}