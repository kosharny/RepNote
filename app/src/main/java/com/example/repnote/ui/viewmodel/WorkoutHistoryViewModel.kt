package com.example.repnote.ui.viewmodel

import com.example.repnote.ui.state.HistoryPeriod
import com.example.repnote.R
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.WorkoutSessionRepository
import com.example.repnote.ui.state.DeleteWorkoutUiState
import com.example.repnote.ui.state.WorkoutHistoryUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class WorkoutHistoryViewModel(
    private val savedStateHandle: androidx.lifecycle.SavedStateHandle,
    private val sessionRepository: WorkoutSessionRepository
): ViewModel() {
    private val _uiState =
        MutableStateFlow<WorkoutHistoryUiState>(
            WorkoutHistoryUiState.Loading
        )

    val uiState: StateFlow<WorkoutHistoryUiState> =
        _uiState.asStateFlow()



    private val _deleteState =
        MutableStateFlow<DeleteWorkoutUiState>(
            DeleteWorkoutUiState.Idle
        )

    val deleteState: StateFlow<DeleteWorkoutUiState> =
        _deleteState.asStateFlow()

    val query = savedStateHandle.getStateFlow("query", "")
    val period = savedStateHandle.getStateFlow("period", HistoryPeriod.ALL.name)

    fun changeQuery(value: String) { savedStateHandle["query"] = value }
    fun changePeriod(value: HistoryPeriod) { savedStateHandle["period"] = value.name }

    private var loadJob: Job? = null


    init {
        reload()
    }

    fun reload() {
        if (loadJob?.isActive == true) return

        _uiState.value = WorkoutHistoryUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                kotlinx.coroutines.flow.combine(sessionRepository.observeSessions(), query, period) { sessions, query, period ->
                    val now = System.currentTimeMillis()
                    val day = 86_400_000L
                    val cutoff = when (period) {
                        "WEEK" -> now - 7 * day
                        "MONTH" -> now - 30 * day
                        else -> Long.MIN_VALUE
                    }
                    val visible = sessions.filter {
                        it.workoutTitle.contains(query.trim(), ignoreCase = true) && it.completedAtEpochMillis >= cutoff
                    }
                    val dates = sessions.map {
                        java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone(it.timeZoneId)).apply { timeInMillis = it.completedAtEpochMillis }.let { date ->
                            date.get(java.util.Calendar.YEAR) to date.get(java.util.Calendar.DAY_OF_YEAR)
                        }
                    }.distinct()
                    WorkoutHistoryUiState.Content(visible, sessions.size,
                        sessions.count { it.completedAtEpochMillis >= now - 7 * day }, dates.size)
                }.collect { _uiState.value = it }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = WorkoutHistoryUiState.Error(
                    message = R.string.error_load_history
                )
            }
        }
    }

    fun deleteSession(sessionId: String) {
        if (_deleteState.value is DeleteWorkoutUiState.Deleting) return
        _deleteState.value = DeleteWorkoutUiState.Deleting(sessionId = sessionId)

        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(sessionId)
                _deleteState.value = DeleteWorkoutUiState.Idle
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _deleteState.value = DeleteWorkoutUiState.Error(
                    message = R.string.error_delete_workout
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[APPLICATION_KEY] as RepNoteApplication

                    WorkoutHistoryViewModel(
                        savedStateHandle = createSavedStateHandle(),
                        sessionRepository = application.sessionRepository
                    )
                }
            }
    }
}
