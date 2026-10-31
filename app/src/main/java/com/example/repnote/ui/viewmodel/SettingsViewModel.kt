package com.example.repnote.ui.viewmodel

import com.example.repnote.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.SettingsRepository
import com.example.repnote.model.ThemeMode
import com.example.repnote.ui.state.SettingsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import com.example.repnote.model.AppLanguage

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SettingsUiState()
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (loadJob?.isActive == true) return

        _uiState.update { current ->
            current.copy(
                isLoading = true,
                loadErrorMessage = null
            )
        }

        loadJob = viewModelScope.launch {
            try {
                combine(settingsRepository.observeThemeMode(), settingsRepository.observeLanguage()) { mode, language ->
                    mode to language
                }.collect { (mode, language) ->
                    _uiState.update { current ->
                        current.copy(
                            themeMode = mode,
                            language = language,
                            isLoading = false,
                            loadErrorMessage = null
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        loadErrorMessage = R.string.error_load_settings
                    )
                }
            }
        }
    }

    fun changeThemeMode(mode: ThemeMode) {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.isSaving ||
            state.loadErrorMessage != null ||
            mode == state.themeMode
        ) {
            return
        }

        _uiState.update { current ->
            current.copy(
                isSaving = true,
                saveErrorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                settingsRepository.setThemeMode(mode)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { current ->
                    current.copy(
                        saveErrorMessage = R.string.error_save_theme
                    )
                }
            } finally {
                _uiState.update { current ->
                    current.copy(isSaving = false)
                }
            }
        }
    }

    fun changeLanguage(language: AppLanguage) {
        val state = _uiState.value
        if (state.isLoading || state.isSaving || state.loadErrorMessage != null || state.language == language) return
        _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
        viewModelScope.launch {
            try {
                settingsRepository.setLanguage(language)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(saveErrorMessage = R.string.error_save_settings) }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    SettingsViewModel(
                        settingsRepository = application.settingsRepository
                    )
                }
            }
    }
}
