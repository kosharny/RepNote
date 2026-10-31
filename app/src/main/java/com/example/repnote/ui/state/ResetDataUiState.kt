package com.example.repnote.ui.state

sealed interface ResetDataUiState {
    data object Idle : ResetDataUiState
    data object Saving : ResetDataUiState
    data object Success : ResetDataUiState
    data object Error : ResetDataUiState
}
