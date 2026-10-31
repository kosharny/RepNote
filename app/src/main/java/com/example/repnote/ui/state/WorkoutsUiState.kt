package com.example.repnote.ui.state

import com.example.repnote.model.WorkoutTemplate

data class WorkoutsUiState(
    val workouts: List<WorkoutTemplate>,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)
