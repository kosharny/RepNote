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
import com.example.repnote.data.repository.WorkoutTemplateRepository
import com.example.repnote.ui.state.RenameWorkoutUiState
import com.example.repnote.ui.state.WorkoutsUiState
import com.example.repnote.ui.state.withLastWorkoutDates
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.example.repnote.ui.state.DeleteTemplateUiState

class WorkoutsViewModel(
    private val templateRepository: WorkoutTemplateRepository,
    private val sessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WorkoutsUiState(
            workouts = emptyList(),
            isLoading = true
        )
    )

    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    private val _renameState =
        MutableStateFlow<RenameWorkoutUiState?>(null)

    val renameState: StateFlow<RenameWorkoutUiState?> =
        _renameState.asStateFlow()

    private val _deleteState =
        MutableStateFlow<DeleteTemplateUiState?>(null)

    val deleteState: StateFlow<DeleteTemplateUiState?> =
        _deleteState.asStateFlow()

    private var loadJob: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (loadJob?.isActive == true) {
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        loadJob = viewModelScope.launch {
            try {
                combine(
                    templateRepository.observeTemplates(),
                    sessionRepository.observeSessions()
                ) { templates, sessions ->
                    WorkoutsUiState(
                        workouts = withLastWorkoutDates(
                            workouts = templates,
                            sessions = sessions
                        ),
                        isLoading = false,
                        errorMessage = null
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = R.string.error_load_workouts
                )
            }
        }
    }

    fun startRename(workoutId: Long) {
        if (_renameState.value?.isSaving == true) return

        if (_deleteState.value != null) return

        val workout = _uiState.value.workouts.firstOrNull { workout ->
            workout.id == workoutId
        } ?: return

        _renameState.value = RenameWorkoutUiState(
            workoutId = workout.id,
            title = workout.title
        )
    }

    fun changeRenameTitle(title: String) {
        val form = _renameState.value ?: return
        if (form.isSaving) return

        _renameState.value = form.copy(
            title = title,
            errorMessage = null
        )
    }

    fun dismissRename() {
        if (_renameState.value?.isSaving == true) return

        _renameState.value = null
    }

    fun saveRename() {
        val form = _renameState.value ?: return
        if (form.isSaving) return

        val trimmedTitle = form.title.trim()

        if (trimmedTitle.isEmpty()) {
            _renameState.value = form.copy(
                errorMessage = R.string.enter_workout_name
            )
            return
        }

        _renameState.value = form.copy(
            isSaving = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                templateRepository.renameTemplate(
                    templateId = form.workoutId,
                    title = trimmedTitle
                )

                _renameState.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _renameState.value = form.copy(
                    isSaving = false,
                    errorMessage = R.string.error_rename_workout
                )
            }
        }
    }

    fun startDeleteTemplate(workoutId: Long) {
        if (_deleteState.value != null) return
        if (_renameState.value != null) return

        val workout = _uiState.value.workouts.firstOrNull { workout ->
            workout.id == workoutId
        } ?: return

        _deleteState.value = DeleteTemplateUiState(
            workoutId = workout.id,
            workoutTitle = workout.title
        )
    }

    fun dismissDeleteTemplate() {
        if (_deleteState.value?.isDeleting == true) return

        _deleteState.value = null
    }

    fun confirmDeleteTemplate() {
        val form = _deleteState.value ?: return
        if (form.isDeleting) return

        _deleteState.value = form.copy(
            isDeleting = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                templateRepository.deleteTemplate(form.workoutId)

                _deleteState.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _deleteState.value = form.copy(
                    isDeleting = false,
                    errorMessage = R.string.error_delete_workout
                )
            }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    WorkoutsViewModel(
                        templateRepository = application.workoutTemplateRepository,
                        sessionRepository = application.sessionRepository
                    )
                }
            }
        }
    }
}
