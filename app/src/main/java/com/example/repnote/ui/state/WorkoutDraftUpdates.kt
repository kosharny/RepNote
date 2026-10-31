package com.example.repnote.ui.state

fun updateDraftEntries(
    draft: WorkoutDraftUiState,
    exerciseId: Long,
    entries: List<SetEntryUiState>
): WorkoutDraftUiState {
    val updated = draft.entriesByExercise + (exerciseId to entries)
    return draft.copy(entriesByExercise = updated)
}