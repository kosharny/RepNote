package com.example.repnote.ui.screens

import com.example.repnote.ui.viewmodel.RestTimerViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.repnote.ui.state.PreviousExerciseUiState
import com.example.repnote.ui.viewmodel.PreviousExerciseViewModel
import com.example.repnote.ui.viewmodel.WorkoutDraftViewModel
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel

@Composable
fun ExerciseEntryRoute(
    exerciseName: String,
    workoutId: Long,
    exerciseId: Long,
    viewModel: WorkoutDraftViewModel,
    initialSetCount: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    exerciseNote: String = "",
    onNext: (() -> Unit)? = null,
    timer: RestTimerViewModel
) {
    val timerState by timer.uiState.collectAsStateWithLifecycle()
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()

    val previousViewModel: PreviousExerciseViewModel =
        composeViewModel(
            key = "previous-exercise-$exerciseId",
            factory = PreviousExerciseViewModel.factory(exerciseId)
        )

    val entries =
        drafts[workoutId]?.entriesByExercise?.get(exerciseId).orEmpty()

    val previousState by
    previousViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(
        viewModel,
        workoutId,
        exerciseId,
        initialSetCount
    ) {
        viewModel.ensureExercise(
            workoutId = workoutId,
            exerciseId = exerciseId,
            initialSetCount = initialSetCount
        )
    }

    ExerciseEntryScreen(
        exerciseName = exerciseName,
        exerciseNote = exerciseNote,
        entries = entries,
        onWeightChange = { entryId, newWeight ->
            viewModel.changeWeight(
                workoutId,
                exerciseId,
                entryId,
                newWeight
            )
        },
        onRepsChange = { entryId, newReps ->
            viewModel.changeReps(
                workoutId,
                exerciseId,
                entryId,
                newReps
            )
        },
        onNext = onNext,
        timerState = timerState,
        onTimerDuration = timer::selectDuration,
        onTimerStart = timer::start,
        onTimerPause = timer::pause,
        onTimerReset = timer::reset,
        onCompletedChange = { entryId, isCompleted ->
            val wasCompleted = entries.firstOrNull { it.id == entryId }?.isCompleted == true
            viewModel.changeCompletion(
                workoutId,
                exerciseId,
                entryId,
                isCompleted
            )
            val completed = viewModel.drafts.value[workoutId]?.entriesByExercise?.get(exerciseId)
                ?.firstOrNull { it.id == entryId }?.isCompleted == true
            if (isCompleted && completed && !wasCompleted) timer.startAfterSet()
        },
        onAddEntry = {
            viewModel.addEntry(
                workoutId,
                exerciseId
            )
        },
        onDeleteEntry = { entryId ->
            viewModel.removeEntry(
                workoutId,
                exerciseId,
                entryId
            )
        },
        previousState = previousState,
        onRetryPrevious = previousViewModel::reload,
        onBack = onBack,
        modifier = modifier
    )
}
