package com.example.repnote.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.repnote.ui.preview.PreviewData
import com.example.repnote.ui.state.RenameWorkoutUiState
import com.example.repnote.ui.state.WorkoutsUiState
import com.example.repnote.ui.theme.RepNoteTheme

@Composable
private fun WorkoutsPreview(
    uiState: WorkoutsUiState,
    darkTheme: Boolean = false
) {
    RepNoteTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            WorkoutsScreen(
                uiState = uiState,
                onWorkoutClick = {},
                onCreateWorkout = {},
                onRetry = {},
                renameState = null,
                onRenameClick = {},
                onRenameTitleChange = {},
                onRenameConfirm = {},
                onRenameDismiss = {},
                drafts = emptyMap(),
                onDiscardDraft = {},
                onEditWorkout = {},
                deleteState = null,
                canDeleteTemplates = true,
                onDeleteClick = {},
                onDeleteConfirm = {},
                onDeleteDismiss = {},
                onCatalogClick = {},
            )
        }
    }
}

@Preview(name = "Тренировки — светлая", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun WorkoutScreenPreview() {
    WorkoutsPreview(WorkoutsUiState(workouts = PreviewData.workouts))
}

@Preview(name = "Тренировки — темная", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun WorkoutScreenDarkPreview() {
    WorkoutsPreview(
        uiState = WorkoutsUiState(workouts = PreviewData.workouts),
        darkTheme = true
    )
}

@Preview(name = "Загрузка дат", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun WorkoutScreenIsLoadingPreview() {
    WorkoutsPreview(
        WorkoutsUiState(
            workouts = PreviewData.workouts.map { workout ->
                workout.copy(lastWorkoutText = "Дата загружается…")
            },
            isLoading = true
        )
    )
}

@Preview(name = "Ошибка загрузки", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun WorkoutScreenErrorMessagePreview() {
    WorkoutsPreview(
        WorkoutsUiState(
            workouts = PreviewData.workouts.map { workout ->
                workout.copy(lastWorkoutText = "Дата недоступна")
            },
            errorMessage = R.string.error_load_workouts
        )
    )
}
