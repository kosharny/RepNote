package com.example.repnote.ui.state

import com.example.repnote.model.Exercise

data class ExerciseCatalogUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = true,
    val loadErrorMessage: Int? = null,
    val editor: ExerciseEditorUiState? = null
)

data class ExerciseEditorUiState(
    val exerciseId: Long? = null,
    val name: String = "",
    val note: String = "",
    val isDeleting: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: Int? = null
)
