package com.example.repnote.ui.state

data class RenameWorkoutUiState(
    val workoutId: Long,
    val title: String,
    val isSaving: Boolean = false,
    val errorMessage: Int? = null
)
