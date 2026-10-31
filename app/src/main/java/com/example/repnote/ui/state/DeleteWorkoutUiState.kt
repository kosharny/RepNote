package com.example.repnote.ui.state

sealed interface DeleteWorkoutUiState {
    data object Idle : DeleteWorkoutUiState

    data class Deleting(
        val sessionId: String
    ) : DeleteWorkoutUiState

    data class Error(
        val message: Int
    ) : DeleteWorkoutUiState
}
