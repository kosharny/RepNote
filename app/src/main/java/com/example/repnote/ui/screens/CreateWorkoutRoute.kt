package com.example.repnote.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.ui.viewmodel.CreateWorkoutViewModel

@Composable
fun CreateWorkoutRoute(
    onCreated: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    templateId: Long? = null
) {
    val createWorkoutViewModel: CreateWorkoutViewModel = viewModel(
        key = templateId?.let { "edit_workout_$it" }
            ?: "create_workout",
        factory = CreateWorkoutViewModel.factory(templateId)
    )

    val uiState by
    createWorkoutViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.savedTemplateId) {
        if (uiState.savedTemplateId != null) {
            onCreated()
        }
    }

    CreateWorkoutScreen(
        uiState = uiState,
        onTitleChange = createWorkoutViewModel::changeTitle,
        onExerciseToggle = createWorkoutViewModel::toggleExercise,
        onNewExerciseNameChange =
            createWorkoutViewModel::changeNewExerciseName,
        onAddExercise = createWorkoutViewModel::addExercise,
        onSave = createWorkoutViewModel::save,
        onBack = onBack,
        onMoveExercise = createWorkoutViewModel::moveExercise,
        onRetryLoad = createWorkoutViewModel::loadForm,
        isEditing = templateId != null,
        onInitialSetCountChange =
            createWorkoutViewModel::changeInitialSetCount,
        modifier = modifier
    )
}
