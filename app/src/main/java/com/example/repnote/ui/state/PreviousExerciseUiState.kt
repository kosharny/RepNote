package com.example.repnote.ui.state

import com.example.repnote.model.PreviousExerciseResult

sealed interface PreviousExerciseUiState {
    data object Loading : PreviousExerciseUiState
    data object Empty : PreviousExerciseUiState

    data class Content(
        val result: PreviousExerciseResult
    ) : PreviousExerciseUiState

    data class Error(
        val message: Int
    ) : PreviousExerciseUiState
}
