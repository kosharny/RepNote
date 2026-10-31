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
import com.example.repnote.ui.state.PreviousExerciseUiState
import com.example.repnote.ui.state.WorkoutResultUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class PreviousExerciseViewModel(
    private val exerciseId: Long,
    private val sessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreviousExerciseUiState>(PreviousExerciseUiState.Loading)

    val uiState: StateFlow<PreviousExerciseUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (loadJob?.isActive == true) return

        _uiState.value = PreviousExerciseUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                val session = sessionRepository.getPreviousExerciseResult(exerciseId)
                if  (session == null) {
                    _uiState.value = PreviousExerciseUiState.Empty
                } else {
                    _uiState.value = PreviousExerciseUiState.Content(session)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = PreviousExerciseUiState.Error(
                    message = R.string.error_load_previous
                )
            }
        }
    }

    companion object {
        fun factory(exerciseId: Long): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    PreviousExerciseViewModel(
                        exerciseId = exerciseId,
                        sessionRepository = application.sessionRepository
                    )
                }
            }
    }
}
