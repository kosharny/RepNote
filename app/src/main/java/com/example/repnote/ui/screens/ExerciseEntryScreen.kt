package com.example.repnote.ui.screens

import com.example.repnote.ui.components.RestTimerCard
import com.example.repnote.ui.viewmodel.RestTimerUiState
import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repnote.model.PreviousExerciseResult
import com.example.repnote.model.CompletedExercise
import com.example.repnote.model.CompletedSet
import com.example.repnote.model.isRepsValid
import com.example.repnote.model.isWeightValid
import com.example.repnote.ui.components.PreviousExerciseBlock
import com.example.repnote.ui.components.SetEntryRow
import com.example.repnote.ui.state.PreviousExerciseUiState
import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.theme.RepNoteTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEntryScreen(
    exerciseName: String,
    entries: List<SetEntryUiState>,
    onWeightChange: (Long, String) -> Unit,
    onRepsChange: (Long, String) -> Unit,
    onCompletedChange: (Long, Boolean) -> Unit,
    onAddEntry: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    previousState: PreviousExerciseUiState,
    onRetryPrevious: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    exerciseNote: String = "",
    timerState: RestTimerUiState = RestTimerUiState(),
    onTimerDuration: (Int) -> Unit = {},
    onTimerStart: () -> Unit = {},
    onTimerPause: () -> Unit = {},
    onTimerReset: () -> Unit = {},
    onNext: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.sets))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "exercise_name") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = exerciseName, style = MaterialTheme.typography.headlineMedium)
                    if (exerciseNote.isNotBlank()) {
                        Text(exerciseNote, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item(key = "rest_timer") {
                RestTimerCard(timerState, onTimerDuration, onTimerStart, onTimerPause, onTimerReset)
            }
            item(key = "previous_result") {
                PreviousExerciseBlock(
                    state = previousState,
                    onRetry = onRetryPrevious
                )
            }

            item(key = "current_header") {
                Text(
                    text = stringResource(R.string.current_sets),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (entries.isEmpty()) {
                item(key = "empty_entries") {
                    Text(stringResource(R.string.empty_sets))
                }
            }

            itemsIndexed(
                items = entries,
                key = { _, entry -> entry.id }
            ) { index, entry ->
                val weightValid = isWeightValid(entry.weightText)
                val repsValid = isRepsValid(entry.repsText)

                val weightError = if (
                    entry.weightText.isNotBlank() && !weightValid
                ) {
                    stringResource(R.string.weight_error)
                } else {
                    null
                }

                val repsError = if (
                    entry.repsText.isNotBlank() && !repsValid
                ) {
                    stringResource(R.string.reps_error)
                } else {
                    null
                }

                SetEntryRow(
                    modifier = Modifier.animateItem(),
                    setNumber = index + 1,
                    entry = entry,
                    onCompletedChange = { value ->
                        onCompletedChange(entry.id, value)
                    },
                    onWeightChange = { value ->
                        onWeightChange(entry.id, value)
                    },
                    onRepsChange = { value ->
                        onRepsChange(entry.id, value)
                    },
                    canComplete = weightValid && repsValid,
                    weightError = weightError,
                    repsError = repsError,
                    onDelete = {
                        onDeleteEntry(entry.id)
                    }
                )
            }

            item(key = "add_entry") {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onAddEntry()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_set))
                }
            }

            item(key = "next") {
                androidx.compose.material3.Button(onClick = onNext ?: onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(if (onNext != null) R.string.next_exercise else R.string.back_to_workout))
                }
            }
            item(key = "completed_count") {
                Text(
                    text = stringResource(R.string.completed_count, entries.count { it.isCompleted })
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun ExerciseEntryScreenLoadingPreview() {

    val entries: List<SetEntryUiState> = listOf(
        SetEntryUiState(id = 1L),
        SetEntryUiState(id = 2L),
        SetEntryUiState(id = 3L),
        )

    RepNoteTheme() {
        ExerciseEntryScreen(
            exerciseName = "Жим лежа",
            entries = entries,
            onWeightChange = { _, _ -> },
            onRepsChange = { _, _ -> },
            onCompletedChange = { _, _ -> },
            onAddEntry = {},
            onDeleteEntry = { _ -> },
            previousState = PreviousExerciseUiState.Loading,
            onRetryPrevious = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun ExerciseEntryScreenErrorPreview() {

    val entries: List<SetEntryUiState> = listOf(
        SetEntryUiState(id = 1L),
        SetEntryUiState(id = 2L),
        SetEntryUiState(id = 3L),
    )

    RepNoteTheme() {
        ExerciseEntryScreen(
            exerciseName = "Жим лежа",
            entries = entries,
            onWeightChange = { _, _ -> },
            onRepsChange = { _, _ -> },
            onCompletedChange = { _, _ -> },
            onAddEntry = {},
            onDeleteEntry = { _ -> },
            previousState = PreviousExerciseUiState.Error(R.string.error_load_previous),
            onRetryPrevious = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun ExerciseEntryScreenEmptyPreview() {

    val entries: List<SetEntryUiState> = listOf(
        SetEntryUiState(id = 1L),
        SetEntryUiState(id = 2L),
        SetEntryUiState(id = 3L),
    )

    RepNoteTheme() {
        ExerciseEntryScreen(
            exerciseName = "Жим лежа",
            entries = entries,
            onWeightChange = { _, _ -> },
            onRepsChange = { _, _ -> },
            onCompletedChange = { _, _ -> },
            onAddEntry = {},
            onDeleteEntry = { _ -> },
            previousState = PreviousExerciseUiState.Empty,
            onRetryPrevious = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun ExerciseEntryScreenErrorContentPreview() {

    val entries: List<SetEntryUiState> = listOf(
        SetEntryUiState(id = 1L),
        SetEntryUiState(id = 2L),
        SetEntryUiState(id = 3L),
    )

    RepNoteTheme() {
        ExerciseEntryScreen(
            exerciseName = "Жим лежа",
            entries = entries,
            onWeightChange = { _, _ -> },
            onRepsChange = { _, _ -> },
            onCompletedChange = { _, _ -> },
            onAddEntry = {},
            onDeleteEntry = { _ -> },
            previousState = PreviousExerciseUiState.Content(
                PreviousExerciseResult(
                    sessionId = "",
                    completedAtEpochMillis = System.currentTimeMillis(),
                    timeZoneId = "Europe/Kyiv",
                    exercise = CompletedExercise(
                        exerciseId = 1L,
                        exerciseName = "Squad",
                        sets = listOf(
                            CompletedSet(
                                weightKg = 120.0,
                                reps = 12
                            )
                        )
                    )
                )
            ),
            onRetryPrevious = {},
            onBack = {},
        )
    }
}
