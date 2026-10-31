package com.example.repnote.ui.state

sealed interface FinishWorkoutUiState {
    data object Idle : FinishWorkoutUiState
    data object Saving : FinishWorkoutUiState
    data class Success(
        val sessionId: String
    ) : FinishWorkoutUiState
    data class Error(
        val message: Int
    ): FinishWorkoutUiState
}
