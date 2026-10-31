package com.example.repnote.ui.screens

import com.example.repnote.ui.util.formatWeight
import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repnote.model.CompletedExercise
import com.example.repnote.model.CompletedSet
import com.example.repnote.model.WorkoutSession
import com.example.repnote.ui.state.WorkoutResultUiState
import com.example.repnote.ui.theme.RepNoteTheme
import com.example.repnote.ui.util.formatSessionDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutResultScreen(
    uiState: WorkoutResultUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.workout_result))
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                WorkoutResultUiState.Loading -> {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.loading_result))
                        }
                    }
                }

                WorkoutResultUiState.NotFound -> {
                    item {
                        Text(stringResource(R.string.session_not_found))
                    }
                }

                is WorkoutResultUiState.Error -> {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(state.message),
                                color = MaterialTheme.colorScheme.error
                            )

                            Button(onClick = onRetry) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }

                is WorkoutResultUiState.Content -> {
                    val session = state.session

                    val totalSetCount = session.exercises.sumOf { exercise ->
                        exercise.sets.size
                    }

                    item(key = "summary") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = session.workoutTitle,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = formatSessionDate(
                                    epochMillis = session.completedAtEpochMillis,
                                    timeZoneId = session.timeZoneId
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = stringResource(R.string.exercise_count, session.exercises.size)
                            )

                            Text(
                                text = stringResource(R.string.completed_count, totalSetCount)
                            )
                        }
                    }

                    items(
                        items = session.exercises,
                        key = { exercise -> exercise.exerciseId }
                    ) { exercise ->
                        CompletedExerciseCard(
                            exercise = exercise,
                            modifier = Modifier.fillMaxWidth().animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedExerciseCard(
    exercise: CompletedExercise,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.completed_count, exercise.sets.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            exercise.sets.forEachIndexed { index, set ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.set_number, index + 1),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = stringResource(R.string.set_result, formatWeight(set.weightKg), set.reps),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutResultScreenPreview() {
    RepNoteTheme {
        WorkoutResultScreen (
            uiState = WorkoutResultUiState.Content(
                session = WorkoutSession(
                    id = "",
                    templateId = 1L,
                    workoutTitle = "Верх тела",
                    completedAtEpochMillis = 1_700_000_000_000L,
                    timeZoneId = "Europe/Kyiv",
                    exercises = listOf(
                        CompletedExercise(
                            exerciseId = 1L,
                            exerciseName = "Жим лежа",
                            sets = listOf(
                                CompletedSet(weightKg = 0.0, reps = 12),
                                CompletedSet(weightKg = 62.5, reps = 8),
                            )
                        ),
                        CompletedExercise(
                            exerciseId = 2L,
                            exerciseName = "Тяга верхнего блока",
                            sets = listOf(
                                CompletedSet(
                                    weightKg = 12.0,
                                    reps = 12
                                ),
                                CompletedSet(
                                    weightKg = 12.0,
                                    reps = 12
                                )
                            )
                        )
                    )
                ),
            ),
            onRetry = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutResultScreenLoadingPreview() {
    RepNoteTheme {
        WorkoutResultScreen (
            uiState = WorkoutResultUiState.Loading,
            onRetry = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutResultScreenNotFoundPreview() {
    RepNoteTheme {
        WorkoutResultScreen (
            uiState = WorkoutResultUiState.NotFound,
            onRetry = {},
            onBack = {},
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutResultScreenErrorPreview() {
    RepNoteTheme {
        WorkoutResultScreen (
            uiState = WorkoutResultUiState.Error(R.string.error_load_result),
            onRetry = {},
            onBack = {},
        )
    }
}
