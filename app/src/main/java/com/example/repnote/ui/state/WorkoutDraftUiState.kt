package com.example.repnote.ui.state

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDraftUiState(
    val workoutId: Long,
    val entriesByExercise: Map<Long, List<SetEntryUiState>> = emptyMap()
)