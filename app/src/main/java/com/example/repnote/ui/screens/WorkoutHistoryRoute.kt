package com.example.repnote.ui.screens

import com.example.repnote.ui.state.HistoryPeriod
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.ui.state.DeleteWorkoutUiState
import com.example.repnote.ui.viewmodel.WorkoutHistoryViewModel
import com.example.repnote.ui.viewmodel.WorkoutResultViewModel


@Composable
fun WorkoutHistoryRoute(
    onSessionClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true
) {
    val viewModel: WorkoutHistoryViewModel = viewModel(
        factory = WorkoutHistoryViewModel.Factory)

    val query by viewModel.query.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    WorkoutHistoryScreen(
        uiState = uiState,
        query = query,
        period = HistoryPeriod.valueOf(period),
        onQueryChange = viewModel::changeQuery,
        onPeriodChange = viewModel::changePeriod,
        onSessionClick = onSessionClick,
        onRetry = viewModel::reload,
        onBack = onBack,
        deleteState = deleteState,
        onDeleteSession = viewModel::deleteSession,
        showBackButton = showBackButton,
        modifier = modifier
    )
}
