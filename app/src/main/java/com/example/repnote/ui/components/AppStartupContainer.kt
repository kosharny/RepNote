package com.example.repnote.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.repnote.ui.screens.AppLoadingScreen
import com.example.repnote.ui.state.DraftStorageUiState
import com.example.repnote.ui.state.SettingsUiState
import com.example.repnote.ui.state.WorkoutsUiState

@Composable
fun AppStartupContainer(
    settingsState: SettingsUiState,
    draftState: DraftStorageUiState,
    workoutsState: WorkoutsUiState,
    onRetrySettings: () -> Unit,
    onRetryDrafts: () -> Unit,
    onRetryWorkouts: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var startupCompleted by rememberSaveable {
        mutableStateOf(false)
    }

    var minimumDisplayElapsed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!minimumDisplayElapsed) {
            delay(1_200L)
            minimumDisplayElapsed = true
        }
    }

    val isLoading = settingsState.isLoading ||
            draftState.isLoading ||
            workoutsState.isLoading

    val errors = listOfNotNull(
        settingsState.loadErrorMessage,
        draftState.loadErrorMessage,
        workoutsState.errorMessage
    )

    val errorMessage = errors.map { stringResource(it) }
        .joinToString(separator = "\n")
        .ifEmpty { null }

    LaunchedEffect(isLoading, errorMessage, minimumDisplayElapsed) {
        if (minimumDisplayElapsed && !isLoading && errorMessage == null) {
            startupCompleted = true
        }
    }

    if (!startupCompleted) {
        AppLoadingScreen(
            modifier = modifier,
            errorMessage = errorMessage,
            onRetry = {
                if (settingsState.loadErrorMessage != null) {
                    onRetrySettings()
                }

                if (draftState.loadErrorMessage != null) {
                    onRetryDrafts()
                }

                if (workoutsState.errorMessage != null) {
                    onRetryWorkouts()
                }
            }
        )

        return
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        content()
    }
}
