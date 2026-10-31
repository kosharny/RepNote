package com.example.repnote.ui.viewmodel

import com.example.repnote.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.WorkoutTemplateRepository
import com.example.repnote.ui.state.CreateWorkoutUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateWorkoutViewModel(
    private val templateRepository: WorkoutTemplateRepository,
    private val templateId: Long? = null
) : ViewModel()  {

    private val _uiState = MutableStateFlow(
        CreateWorkoutUiState()
    )

    private var loadJob: Job? = null
    private var formInitialized = false

    val uiState: StateFlow<CreateWorkoutUiState> = _uiState.asStateFlow()

    init {
        loadForm()
    }

    fun loadForm() {
        if (loadJob?.isActive == true) return

        _uiState.update { current ->
            current.copy(
                isLoading = true,
                loadErrorMessage = null
            )
        }

        loadJob = viewModelScope.launch {
            try {
                if (!formInitialized) {
                    val id = templateId

                    if (id != null) {
                        val template = checkNotNull(
                            templateRepository.getTemplateById(id)
                        ) {
                            R.string.workout_not_found
                        }

                        _uiState.update { current ->
                            current.copy(
                                title = template.title,
                                selectedExerciseIds = template.exercises.map { exercise ->
                                    exercise.id
                                },
                                initialSetCounts = template.initialSetCounts
                            )
                        }
                    }

                    formInitialized = true
                }

                templateRepository.observeExercises().collect { exercises ->
                    _uiState.update { current ->
                        current.copy(
                            availableExercises = exercises,
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
                        loadErrorMessage = R.string.error_load_form
                    )
                }
            }
        }
    }

    fun changeTitle(title: String) {
        _uiState.update { current ->
            if (current.isSaving || current.isAddingExercise) {
                return@update current
            }

            current.copy(
                title = title,
                errorMessage = null
            )
        }
    }

    fun changeNewExerciseName(name: String) {
        _uiState.update { current ->
            if (current.isSaving || current.isAddingExercise) {
                return@update current
            }

            current.copy(
                newExerciseName = name,
                errorMessage = null
            )
        }
    }

    fun toggleExercise(exerciseId: Long) {
        _uiState.update { current ->
            if (current.isSaving || current.isAddingExercise) {
                return@update current
            }

            val exerciseExists = current.availableExercises.any { exercise ->
                exercise.id == exerciseId
            }

            if (!exerciseExists) {
                return@update current
            }

            val selectedIds = if (exerciseId in current.selectedExerciseIds) {
                current.selectedExerciseIds - exerciseId
            } else {
                current.selectedExerciseIds + exerciseId
            }

            current.copy(
                selectedExerciseIds = selectedIds,
                errorMessage = null
            )
        }
    }

    fun addExercise() {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.isSaving ||
            state.isAddingExercise ||
            state.loadErrorMessage != null ||
            state.savedTemplateId != null
        ) {
            return
        }

        val name = state.newExerciseName.trim()

        if (name.isEmpty()) {
            _uiState.update { current ->
                current.copy(
                    errorMessage = R.string.enter_exercise_name
                )
            }
            return
        }

        _uiState.update { current ->
            current.copy(
                isAddingExercise = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val exerciseId = templateRepository.addExercise(name)

                // Не добавляем в выбор ID неудачной вставки.
                check(exerciseId > 0L) {
                    "Упражнение не было добавлено"
                }

                _uiState.update { current ->
                    current.copy(
                        selectedExerciseIds = (
                                current.selectedExerciseIds + exerciseId
                                ).distinct(),
                        newExerciseName = ""
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { current ->
                    current.copy(
                        errorMessage = R.string.error_add_exercise
                    )
                }
            } finally {
                _uiState.update { current ->
                    current.copy(
                        isAddingExercise = false
                    )
                }
            }
        }
    }

    fun save() {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.isSaving ||
            state.isAddingExercise ||
            state.loadErrorMessage != null ||
            state.savedTemplateId != null
        ) {
            return
        }

        val title = state.title.trim()

        if (title.isEmpty()) {
            _uiState.update { current ->
                current.copy(
                    errorMessage = R.string.enter_workout_name
                )
            }
            return
        }

        if (state.selectedExerciseIds.isEmpty()) {
            _uiState.update { current ->
                current.copy(
                    errorMessage = R.string.select_exercise
                )
            }
            return
        }

        val selectionReady = state.selectedExerciseIds.all { selectedId ->
            state.availableExercises.any { exercise ->
                exercise.id == selectedId
            }
        }

        if (!selectionReady) return

        _uiState.update { current ->
            current.copy(
                isSaving = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val exerciseIds = state.selectedExerciseIds.toList()
                val initialSetCounts = exerciseIds.associateWith { exerciseId ->
                    state.initialSetCounts[exerciseId] ?: 3
                }
                val editingId = templateId

                val savedId = if (editingId == null) {
                    templateRepository.createTemplate(
                        title = title,
                        exerciseIds = exerciseIds,
                        initialSetCounts = initialSetCounts
                    )
                } else {
                    templateRepository.updateTemplate(
                        templateId = editingId,
                        title = title,
                        exerciseIds = exerciseIds,
                        initialSetCounts = initialSetCounts
                    )

                    editingId
                }

                _uiState.update { current ->
                    current.copy(
                        savedTemplateId = savedId
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { current ->
                    current.copy(
                        errorMessage = R.string.error_save_workout
                    )
                }
            } finally {
                _uiState.update { current ->
                    current.copy(
                        isSaving = false
                    )
                }
            }
        }
    }

    fun moveExercise(
        exerciseId: Long,
        offset: Int
    ) {
        if (offset != -1 && offset != 1) return

        _uiState.update { current ->
            if (
                current.isLoading ||
                current.isSaving ||
                current.isAddingExercise ||
                current.savedTemplateId != null
            ) {
                return@update current
            }

            val currentIndex =
                current.selectedExerciseIds.indexOf(exerciseId)

            if (currentIndex == -1) {
                return@update current
            }

            val targetIndex = currentIndex + offset

            if (targetIndex !in current.selectedExerciseIds.indices) {
                return@update current
            }

            val reorderedIds = current.selectedExerciseIds.toMutableList()

            val movedId = reorderedIds.removeAt(currentIndex)
            reorderedIds.add(targetIndex, movedId)

            current.copy(
                selectedExerciseIds = reorderedIds.toList(),
                errorMessage = null
            )
        }
    }

    fun changeInitialSetCount(
        exerciseId: Long,
        delta: Int
    ) {
        if (delta != -1 && delta != 1) return

        _uiState.update { current ->
            if (
                current.isLoading ||
                current.isSaving ||
                current.isAddingExercise ||
                current.loadErrorMessage != null ||
                current.savedTemplateId != null
            ) {
                return@update current
            }

            if (exerciseId !in current.selectedExerciseIds) {
                return@update current
            }

            val oldCount = current.initialSetCounts[exerciseId] ?: 3
            val newCount = oldCount + delta

            if (newCount !in 1..10) {
                return@update current
            }

            current.copy(
                initialSetCounts = current.initialSetCounts +
                        (exerciseId to newCount),
                errorMessage = null
            )
        }
    }

    companion object {
        fun factory(
            templateId: Long? = null
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    CreateWorkoutViewModel(
                        templateRepository =
                            application.workoutTemplateRepository,
                        templateId = templateId
                    )
                }
            }
        }
    }
}
