package com.example.repnote.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.ui.viewmodel.WorkoutResultViewModel

@Composable
fun WorkoutResultRoute(
    sessionId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: WorkoutResultViewModel = viewModel(
        key = sessionId,
        factory = WorkoutResultViewModel.factory(sessionId)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WorkoutResultScreen(
        uiState = uiState,
        onRetry = viewModel::reload,
        onBack = onBack,
        modifier = modifier
    )
}