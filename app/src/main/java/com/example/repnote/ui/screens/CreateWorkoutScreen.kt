package com.example.repnote.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.ui.state.CreateWorkoutUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    uiState: CreateWorkoutUiState,
    onTitleChange: (String) -> Unit,
    onExerciseToggle: (Long) -> Unit,
    onNewExerciseNameChange: (String) -> Unit,
    onAddExercise: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onMoveExercise: (Long, Int) -> Unit,
    onRetryLoad: () -> Unit,
    onInitialSetCountChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredExercises = uiState.availableExercises.filter { it.name.contains(query.trim(), ignoreCase = true) }
    val isWriting = uiState.isSaving || uiState.isAddingExercise
    val canEdit = !isWriting &&
            !uiState.isLoading &&
            uiState.loadErrorMessage == null &&
            uiState.savedTemplateId == null

    val canAddExercise = canEdit &&
            !uiState.isLoading &&
            uiState.newExerciseName.isNotBlank()

    val selectionReady = uiState.selectedExerciseIds.all { selectedId ->
        uiState.availableExercises.any { exercise ->
            exercise.id == selectedId
        }
    }

    val canSave = canEdit &&
            !uiState.isLoading &&
            uiState.title.isNotBlank() &&
            uiState.selectedExerciseIds.isNotEmpty() &&
            selectionReady

    val exercisesById = uiState.availableExercises.associateBy { exercise ->
        exercise.id
    }

    val selectedExercises = uiState.selectedExerciseIds.mapNotNull { id ->
        exercisesById[id]
    }

    val canReorder = canEdit &&
            !uiState.isLoading &&
            selectionReady

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            stringResource(R.string.edit_workout_title)
                        } else {
                            stringResource(R.string.create_workout_title)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        enabled = !isWriting
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.create_workout_back
                            )
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
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.loadErrorMessage?.let { message ->
                item(key = "load_error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(message),
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetryLoad) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
            item(key = "plan_summary") {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.fillMaxWidth().animateContentSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (androidx.compose.ui.platform.LocalDensity.current.fontScale <= 1.3f) {
                            com.example.repnote.ui.components.TrainingArtwork(
                                Modifier.fillMaxWidth().height(112.dp), com.example.repnote.ui.components.TrainingArtworkKind.BUILDER)
                        }
                        Text(stringResource(R.string.build_your_plan), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.plan_summary, selectedExercises.size,
                            selectedExercises.sumOf { uiState.initialSetCounts[it.id] ?: 3 }))
                        Text(stringResource(R.string.plan_hint), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item(key = "title") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canEdit,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.create_workout_name)
                        )
                    }
                )
            }

            if (selectedExercises.isNotEmpty()) {
                item(key = "selected_exercises_header") {
                    Text(
                        text = stringResource(R.string.exercise_order),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                itemsIndexed(
                    items = selectedExercises,
                    key = { _, exercise ->
                        "selected_${exercise.id}"
                    }
                ) { index, exercise ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().animateItem().animateContentSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${index + 1}. ${exercise.name}", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { onExerciseToggle(exercise.id) }, enabled = canEdit) {
                                    Icon(Icons.Default.Close, stringResource(R.string.remove_from_plan))
                                }
                            }
                            val initialCount = uiState.initialSetCounts[exercise.id] ?: 3
                            Text(stringResource(R.string.initial_sets, initialCount), style = MaterialTheme.typography.bodyMedium)
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onInitialSetCountChange(exercise.id, -1) }, enabled = canReorder && initialCount > 1) {
                                    Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_remove), stringResource(R.string.less))
                                }
                                Text(initialCount.toString(), style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { onInitialSetCountChange(exercise.id, 1) }, enabled = canReorder && initialCount < 10) {
                                    Icon(Icons.Default.Add, stringResource(R.string.more))
                                }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { onMoveExercise(exercise.id, -1) }, enabled = canReorder && index > 0) {
                                    Icon(Icons.Default.KeyboardArrowUp, stringResource(R.string.move_up))
                                }
                                IconButton(onClick = { onMoveExercise(exercise.id, 1) }, enabled = canReorder && index < selectedExercises.lastIndex) {
                                    Icon(Icons.Default.KeyboardArrowDown, stringResource(R.string.move_down))
                                }
                            }
                        }
                    }
                }
            }

            item(key = "exercises_header") {
                Text(
                    text = stringResource(R.string.create_workout_exercises),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.isLoading) {
                item(key = "loading") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Text(
                            text = stringResource(R.string.create_workout_loading)
                        )
                    }
                }
            } else if (uiState.availableExercises.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = stringResource(R.string.create_workout_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item(key = "search") {
                OutlinedTextField(value = query, onValueChange = { query = it }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.search_exercises)) })
            }
            if (filteredExercises.isEmpty() && uiState.availableExercises.isNotEmpty()) item(key = "no_matches") {
                Text(stringResource(R.string.no_matches))
            }
            items(
                items = filteredExercises,
                key = { exercise ->
                    "exercise_${exercise.id}"
                }
            ) { exercise ->
                val isSelected = exercise.id in uiState.selectedExerciseIds
                val canToggle = canEdit && !uiState.isLoading

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .toggleable(
                            value = isSelected,
                            enabled = canToggle,
                            role = Role.Checkbox,
                            onValueChange = {
                                onExerciseToggle(exercise.id)
                            }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        enabled = canToggle
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item(key = "new_exercise_name") {
                OutlinedTextField(
                    value = uiState.newExerciseName,
                    onValueChange = onNewExerciseNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canEdit,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(
                                R.string.create_workout_new_exercise
                            )
                        )
                    }
                )
            }

            item(key = "add_exercise") {
                OutlinedButton(
                    onClick = onAddExercise,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canAddExercise
                ) {
                    Text(
                        text = stringResource(
                            if (uiState.isAddingExercise) {
                                R.string.create_workout_adding
                            } else {
                                R.string.create_workout_add_exercise
                            }
                        )
                    )
                }
            }

            val errorMessage = uiState.errorMessage

            if (errorMessage != null) {
                item(key = "error") {
                    Text(
                        text = stringResource(errorMessage),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item(key = "save") {
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSave
                ) {
                    Text(
                        text = stringResource(
                            if (uiState.isSaving) {
                                R.string.create_workout_saving
                            } else {
                                R.string.create_workout_save
                            }
                        )
                    )
                }
            }
        }
    }
}
