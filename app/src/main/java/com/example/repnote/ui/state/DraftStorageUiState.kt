package com.example.repnote.ui.state

data class DraftStorageUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val loadErrorMessage: Int? = null,
    val saveErrorMessage: Int? = null
)
