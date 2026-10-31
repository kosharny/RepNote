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
import com.example.repnote.ui.state.ExerciseCatalogUiState
import com.example.repnote.ui.state.ExerciseEditorUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseCatalogViewModel(
    private val repository: WorkoutTemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseCatalogUiState())
    val uiState = _uiState.asStateFlow()

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
                repository.observeExercises().collect { exercises ->
                    _uiState.update { current ->
                        current.copy(
                            exercises = exercises,
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
                        loadErrorMessage = R.string.error_load_exercises
                    )
                }
            }
        }
    }

    fun startEdit(exerciseId: Long? = null) {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.loadErrorMessage != null ||
            state.editor != null
        ) {
            return
        }

        if (exerciseId == null) {
            setEditor(ExerciseEditorUiState())
            return
        }

        val exercise = state.exercises.firstOrNull {
            it.id == exerciseId
        } ?: return

        setEditor(
            ExerciseEditorUiState(
                exerciseId = exercise.id,
                name = exercise.name,
                note = exercise.note
            )
        )
    }

    fun startDelete(exerciseId: Long) {
        val state = _uiState.value
        if (state.editor != null) return

        val exercise = state.exercises.firstOrNull {
            it.id == exerciseId
        } ?: return

        setEditor(
            ExerciseEditorUiState(
                exerciseId = exercise.id,
                name = exercise.name,
                note = exercise.note,
                isDeleting = true
            )
        )
    }

    fun changeName(name: String) {
        val editor = _uiState.value.editor ?: return
        if (editor.isSaving || editor.isDeleting) return

        setEditor(editor.copy(name = name, errorMessage = null))
    }

    fun changeNote(note: String) {
        val editor = _uiState.value.editor ?: return
        if (editor.isSaving || editor.isDeleting) return

        setEditor(editor.copy(note = note, errorMessage = null))
    }

    fun dismissEditor() {
        if (_uiState.value.editor?.isSaving == true) return
        setEditor(null)
    }

    fun save() {
        val editor = _uiState.value.editor ?: return
        if (editor.isSaving || editor.isDeleting) return

        if (editor.name.isBlank()) {
            setEditor(
                editor.copy(errorMessage = R.string.enter_exercise_name)
            )
            return
        }

        performAction { form ->
            val id = form.exerciseId

            if (id == null) {
                val createdId = repository.addExercise(
                    name = form.name,
                    note = form.note
                )
                check(createdId > 0L)
            } else {
                repository.updateExercise(
                    exerciseId = id,
                    name = form.name,
                    note = form.note
                )
            }

            null
        }
    }

    fun delete() {
        val editor = _uiState.value.editor ?: return
        if (!editor.isDeleting || editor.exerciseId == null) return

        performAction { form ->
            val deleted = repository.deleteUnusedExercise(
                checkNotNull(form.exerciseId)
            )

            if (deleted) {
                null
            } else {
                R.string.exercise_in_use
            }
        }
    }

    private fun performAction(
        action: suspend (ExerciseEditorUiState) -> Int?
    ) {
        val editor = _uiState.value.editor ?: return
        if (editor.isSaving) return

        setEditor(editor.copy(isSaving = true, errorMessage = null))

        viewModelScope.launch {
            try {
                val errorMessage = action(editor)

                setEditor(
                    if (errorMessage == null) {
                        null
                    } else {
                        editor.copy(
                            isSaving = false,
                            errorMessage = errorMessage
                        )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                setEditor(
                    editor.copy(
                        isSaving = false,
                        errorMessage = R.string.error_operation
                    )
                )
            }
        }
    }

    private fun setEditor(editor: ExerciseEditorUiState?) {
        _uiState.update { current ->
            current.copy(editor = editor)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[APPLICATION_KEY] as RepNoteApplication

                ExerciseCatalogViewModel(
                    repository = application.workoutTemplateRepository
                )
            }
        }
    }
}
