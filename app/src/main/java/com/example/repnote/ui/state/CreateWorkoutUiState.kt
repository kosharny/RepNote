package com.example.repnote.ui.state

import com.example.repnote.model.Exercise

data class CreateWorkoutUiState(
    val title: String = "",
    val availableExercises: List<Exercise> = emptyList(),
    val selectedExerciseIds: List<Long> = emptyList(),
    val newExerciseName: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isAddingExercise: Boolean = false,
    val errorMessage: Int? = null,
    val savedTemplateId: Long? = null,
    val loadErrorMessage: Int? = null,
    val initialSetCounts: Map<Long, Int> = emptyMap(),
)
