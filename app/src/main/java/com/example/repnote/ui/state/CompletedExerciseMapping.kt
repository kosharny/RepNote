package com.example.repnote.ui.state

import com.example.repnote.model.CompletedExercise
import com.example.repnote.model.Exercise

fun toCompletedExercise(
    exercise: Exercise,
    entries: List<SetEntryUiState>
): CompletedExercise? {
    val completedSets = entries.mapNotNull { toCompletedSet(it) }
    if (completedSets.isEmpty()) return null
    return CompletedExercise(
        exerciseId = exercise.id,
        exerciseName = exercise.name,
        sets = completedSets
    )
}