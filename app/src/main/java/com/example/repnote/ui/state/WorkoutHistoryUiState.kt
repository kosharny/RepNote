package com.example.repnote.ui.state

import com.example.repnote.model.WorkoutSessionSummary

sealed interface WorkoutHistoryUiState {
    data object Loading : WorkoutHistoryUiState

    data class Content(
        val sessions: List<WorkoutSessionSummary>,
        val totalSessions: Int = sessions.size,
        val sessionsThisWeek: Int = 0,
        val activeDays: Int = 0
    ) : WorkoutHistoryUiState

    data class Error(
        val message: Int
    ) : WorkoutHistoryUiState
}
enum class HistoryPeriod { ALL, WEEK, MONTH }
