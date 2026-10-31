package com.example.repnote.ui.state

data class WorkoutPlansUiState(
    val isLoading: Boolean = true,
    val installedPlanKeys: Set<String> = emptySet(),
    val installedSchedules: Map<String, Map<Int, Int>> = emptyMap(),
    val selectedDays: List<Int> = emptyList(),
    val isImporting: Boolean = false,
    val importedPlanKey: String? = null,
    val errorMessage: Int? = null,
    val loadErrorMessage: Int? = null
)
