package com.example.repnote.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.repnote.ui.preview.PreviewData
import com.example.repnote.ui.state.CreateWorkoutUiState
import com.example.repnote.ui.theme.RepNoteTheme

private val filledPreviewState = CreateWorkoutUiState(
    title = "Верх тела",
    availableExercises = PreviewData.exercises.take(2),
    selectedExerciseIds = listOf(2L, 1L),
    isLoading = false
)

@Composable
private fun CreateWorkoutPreview(uiState: CreateWorkoutUiState) {
    RepNoteTheme {
        CreateWorkoutScreen(
            uiState = uiState,
            onTitleChange = {},
            onExerciseToggle = {},
            onNewExerciseNameChange = {},
            onAddExercise = {},
            onSave = {},
            onBack = {},
            onMoveExercise = { _, _ -> },
            onRetryLoad = {},
            onInitialSetCountChange = { _, _ -> },
        )
    }
}

@Preview(name = "Пустая форма", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CreateWorkoutEmptyPreview() {
    CreateWorkoutPreview(
        filledPreviewState.copy(
            title = "",
            selectedExerciseIds = emptyList()
        )
    )
}

@Preview(name = "Заполненная форма", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CreateWorkoutFilledPreview() {
    CreateWorkoutPreview(filledPreviewState)
}

@Preview(name = "Загрузка", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CreateWorkoutLoadingPreview() {
    CreateWorkoutPreview(CreateWorkoutUiState())
}

@Preview(name = "Сохранение", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CreateWorkoutSavingPreview() {
    CreateWorkoutPreview(filledPreviewState.copy(isSaving = true))
}

@Preview(name = "Ошибка сохранения", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CreateWorkoutErrorPreview() {
    CreateWorkoutPreview(
        filledPreviewState.copy(
            errorMessage = R.string.error_save_workout
        )
    )
}
