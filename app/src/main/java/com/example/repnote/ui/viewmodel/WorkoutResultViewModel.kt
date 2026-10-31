package com.example.repnote.ui.viewmodel

import com.example.repnote.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.WorkoutSessionRepository
import com.example.repnote.ui.state.FinishWorkoutUiState
import com.example.repnote.ui.state.WorkoutResultUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class WorkoutResultViewModel(
    private val sessionId: String,
    private val sessionRepository: WorkoutSessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<WorkoutResultUiState>(WorkoutResultUiState.Loading)
    val uiState: StateFlow<WorkoutResultUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (loadJob?.isActive == true) return

        _uiState.value = WorkoutResultUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                val session = sessionRepository.getSessionById(sessionId)
                if  (session == null) {
                    _uiState.value = WorkoutResultUiState.NotFound
                } else {
                    _uiState.value = WorkoutResultUiState.Content(session)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = WorkoutResultUiState.Error(
                    message = R.string.error_load_result
                )
            }
        }
    }

    companion object {
        fun factory(sessionId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    WorkoutResultViewModel(
                        sessionId = sessionId,
                        sessionRepository = application.sessionRepository
                    )
                }
            }
    }
}
