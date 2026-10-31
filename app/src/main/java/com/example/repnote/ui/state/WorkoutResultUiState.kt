package com.example.repnote.ui.state

import com.example.repnote.model.WorkoutSession

sealed interface WorkoutResultUiState {
    data object Loading : WorkoutResultUiState
    data object NotFound : WorkoutResultUiState

    data class Content(
        val session: WorkoutSession
    ) : WorkoutResultUiState

    data class Error(
        val message: Int
    ) : WorkoutResultUiState
}
