package com.example.repnote.ui.state

import com.example.repnote.model.AppLanguage
import com.example.repnote.model.ThemeMode

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.SYSTEM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val loadErrorMessage: Int? = null,
    val saveErrorMessage: Int? = null
)
