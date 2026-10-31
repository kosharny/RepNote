package com.example.repnote.ui.state

data class DeleteTemplateUiState(
    val workoutId: Long,
    val workoutTitle: String,
    val isDeleting: Boolean = false,
    val errorMessage: Int? = null
)
