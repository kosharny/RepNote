package com.example.repnote.ui.screens

import com.example.repnote.ui.components.RestTimerCard
import com.example.repnote.ui.util.formatSessionDate
import com.example.repnote.ui.viewmodel.RestTimerUiState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.repnote.model.WorkoutTemplate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.tooling.preview.Preview
import com.example.repnote.model.Exercise
import com.example.repnote.ui.state.FinishWorkoutUiState
import com.example.repnote.ui.theme.RepNoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workout: WorkoutTemplate?,
    onBack: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    onFinish: () -> Unit,
    completedSetCount: Int,
    finishState: FinishWorkoutUiState,
    modifier: Modifier = Modifier,
    completedByExercise: Map<Long, Int> = emptyMap(),
    timerState: RestTimerUiState = RestTimerUiState(),
    onTimerDuration: (Int) -> Unit = {}, onTimerStart: () -> Unit = {},
    onTimerPause: () -> Unit = {}, onTimerReset: () -> Unit = {}
) {
    val isSaving = finishState == FinishWorkoutUiState.Saving
    val errorMessage =
        (finishState as? FinishWorkoutUiState.Error)?.message

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title =  {
                Text(stringResource(R.string.workout_details))
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    enabled = !isSaving
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (workout != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        workout.scheduledDayOfWeek?.let { day ->
                            Text(stringResource(com.example.repnote.ui.util.weekdayNameRes(day)),
                                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(workout.title, style = MaterialTheme.typography.headlineMedium)
                        Text(stringResource(R.string.exercise_count, workout.exerciseCount))
                        Text(workout.lastCompletedAtEpochMillis?.let {
                        stringResource(R.string.last_workout, formatSessionDate(it, workout.lastTimeZoneId))
                    } ?: stringResource(R.string.no_workouts_yet))
                        Text(stringResource(R.string.completed_count, completedSetCount))
                    }
                }

                item(key = "rest_timer") {
                    RestTimerCard(timerState, onTimerDuration, onTimerStart, onTimerPause, onTimerReset)
                }
                items(
                    items = workout.exercises,
                    key = { exercise -> exercise.id }
                ) { exercise ->
                    OutlinedCard(
                        onClick = {
                            onExerciseClick(exercise.id)
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth().animateItem()
                    ) {
                        Text(
                            text = exercise.name,
                            modifier = Modifier.padding(16.dp)
                        )
                        if (exercise.note.isNotBlank()) {
                            Text(exercise.note, modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(stringResource(R.string.exercise_progress, completedByExercise[exercise.id] ?: 0,
                            workout.initialSetCounts[exercise.id] ?: 3),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                item {
                    Text(stringResource(R.string.workout_not_found))
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = stringResource(errorMessage),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = onFinish,
                        enabled = completedSetCount > 0 && !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSaving) {
                            Text(stringResource(R.string.create_workout_saving))
                        } else {
                            Text(stringResource(R.string.finish_workout))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutDetailsScreenPreview() {
    RepNoteTheme {
        WorkoutDetailsScreen(
            workout = WorkoutTemplate(
                0L,
                "Верх тела",
                listOf(
                    Exercise(id = 1L, name = "Жим лежа"),
                    Exercise(id = 2L, name = "Тяга верхнего блока")
                ),
                "Последняя тренировка: вчера",
            ),
            onBack = { },
            onExerciseClick = { _ -> },
            onFinish = {},
            finishState = FinishWorkoutUiState.Idle,
            completedSetCount = 4,
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutDetailsScreenPreview2() {
    RepNoteTheme {
        WorkoutDetailsScreen(
            workout = WorkoutTemplate(
                0L,
                "Верх тела",
                listOf(
                    Exercise(id = 1L, name = "Жим лежа"),
                    Exercise(id = 2L, name = "Тяга верхнего блока")
                ),
                "Последняя тренировка: вчера",
            ),
            onBack = { },
            onExerciseClick = { _ -> },
            onFinish = {},
            finishState = FinishWorkoutUiState.Saving,
            completedSetCount = 4,
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutDetailsScreenPreview3() {
    RepNoteTheme {
        WorkoutDetailsScreen(
            workout = WorkoutTemplate(
                0L,
                "Верх тела",
                listOf(
                    Exercise(id = 1L, name = "Жим лежа"),
                    Exercise(id = 2L, name = "Тяга верхнего блока")
                ),
                "Последняя тренировка: вчера",
            ),
            onBack = { },
            onExerciseClick = { _ -> },
            onFinish = {},
            finishState = FinishWorkoutUiState.Error(
                message = R.string.error_save_workout
            ),
            completedSetCount = 4,
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun WorkoutEmptyDetailsScreenPreview() {
    RepNoteTheme {
        WorkoutDetailsScreen(
            workout = null,
            onBack = { },
            onExerciseClick = { _ -> },
            onFinish = {},
            finishState = FinishWorkoutUiState.Idle,
            completedSetCount = 0,
        )
    }
}
