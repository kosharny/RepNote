package com.example.repnote.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.R
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.plans.WorkoutPlanCatalog
import com.example.repnote.data.repository.WorkoutTemplateRepository
import com.example.repnote.model.WorkoutPlanImport
import com.example.repnote.ui.state.WorkoutPlansUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutPlansViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: WorkoutTemplateRepository,
    private val planKey: String?
) : ViewModel() {
    private val definition = planKey?.let(WorkoutPlanCatalog::find)
    private val _uiState = MutableStateFlow(WorkoutPlansUiState(
        selectedDays = savedStateHandle.get<ArrayList<Int>>("days")?.toList()
            ?: definition?.defaultDays.orEmpty()
    ))
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init { reload() }

    fun reload() {
        if (loadJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, loadErrorMessage = null) }
        loadJob = viewModelScope.launch {
            try {
                repository.observeTemplates().collect { templates ->
                    _uiState.update { it.copy(isLoading = false,
                        installedPlanKeys = templates.mapNotNull { workout -> workout.sourcePlanKey }.toSet(),
                        installedSchedules = templates.filter { workout -> workout.sourcePlanKey != null && workout.scheduledDayOfWeek != null }
                            .groupBy { workout -> checkNotNull(workout.sourcePlanKey) }
                            .mapValues { (_, workouts) -> workouts.associate { workout -> workout.planPosition to checkNotNull(workout.scheduledDayOfWeek) } }) }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, loadErrorMessage = R.string.plan_load_error) }
            }
        }
    }

    fun toggleDay(day: Int) {
        val definition = definition ?: return
        val state = _uiState.value
        if (state.isImporting || state.importedPlanKey != null || day !in 1..7) return
        val days = if (day in state.selectedDays) state.selectedDays - day
            else if (state.selectedDays.size < definition.workouts.size) state.selectedDays + day else return
        savedStateHandle["days"] = ArrayList(days.sorted())
        _uiState.update { it.copy(selectedDays = days.sorted(), errorMessage = null) }
    }

    fun importPlan(plan: WorkoutPlanImport) {
        val state = _uiState.value
        if (state.isImporting || state.isLoading || state.loadErrorMessage != null ||
            state.importedPlanKey != null || plan.key != planKey) return
        if (plan.key in state.installedPlanKeys) return
        if (state.selectedDays.size != definition?.workouts?.size) return
        _uiState.update { it.copy(isImporting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val added = repository.importPlan(plan)
                _uiState.update { it.copy(isImporting = false,
                    importedPlanKey = plan.key.takeIf { added },
                    errorMessage = if (added) null else R.string.plan_already_added) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isImporting = false, errorMessage = R.string.plan_import_error) }
            }
        }
    }

    companion object {
        fun factory(planKey: String? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WorkoutPlansViewModel(createSavedStateHandle(),
                    (this[APPLICATION_KEY] as RepNoteApplication).workoutTemplateRepository, planKey)
            }
        }
    }
}
