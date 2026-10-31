package com.example.repnote.ui.state

fun countCompletedSets(
    draft: WorkoutDraftUiState?
): Int {
    if (draft == null) return 0
    val exercises = draft.entriesByExercise.values
    val total = exercises.sumOf { exercises -> exercises.count { entry -> entry.isCompleted } }
    return total
}