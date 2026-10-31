package com.example.repnote.ui.screens

import com.example.repnote.ui.components.TrainingArtwork
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.repnote.ui.components.WorkoutCard
import com.example.repnote.ui.state.WorkoutsUiState
import com.example.repnote.R
import com.example.repnote.ui.state.RenameWorkoutUiState
import com.example.repnote.ui.state.WorkoutDraftUiState
import com.example.repnote.ui.state.countCompletedSets
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.repnote.ui.state.DeleteTemplateUiState

@Composable
fun WorkoutsScreen(
    uiState: WorkoutsUiState,
    onWorkoutClick: (Long) -> Unit,
    onCreateWorkout: () -> Unit,
    onRetry: () -> Unit,
    renameState: RenameWorkoutUiState?,
    onRenameClick: (Long) -> Unit,
    onRenameTitleChange: (String) -> Unit,
    onRenameConfirm: () -> Unit,
    onRenameDismiss: () -> Unit,
    drafts: Map<Long, WorkoutDraftUiState>,
    onDiscardDraft: (Long) -> Unit,
    onEditWorkout: (Long) -> Unit,
    deleteState: DeleteTemplateUiState?,
    canDeleteTemplates: Boolean,
    onDeleteClick: (Long) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onCatalogClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBrowsePlans: () -> Unit = {}
) {

    var pendingDiscardId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        item(key = "workouts_header") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.workouts),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = stringResource(R.string.workouts_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "artwork") {
            TrainingArtwork(Modifier.fillMaxWidth().height(128.dp))
        }
        item(key = "exercise_catalog") {
            Button(
                onClick = onCatalogClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.exercise_catalog))
            }
        }

        if (uiState.isLoading) {
            item {
                Column(verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_workouts))
                }
            }
        }
        if (uiState.errorMessage != null) {
            item {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(stringResource(uiState.errorMessage))
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
        if (
            !uiState.isLoading &&
            uiState.errorMessage == null &&
            uiState.workouts.isEmpty()
        ) {
            item(key = "empty_workouts") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.empty_workouts),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = stringResource(R.string.empty_workouts_hint)
                    )
                    androidx.compose.material3.FilledTonalButton(onClick = onBrowsePlans) {
                        Text(stringResource(R.string.workout_plans))
                    }
                }
            }
        }
        items(
            items = uiState.workouts,
            key = { workout -> workout.id }
        ) { workout ->
            val draft = drafts[workout.id]

            WorkoutCard(
                workout = workout,
                hasDraft = draft != null,
                completedSetCount = countCompletedSets(draft),
                onClick = {
                    onWorkoutClick(workout.id)
                },
                onRename = {
                    onRenameClick(workout.id)
                },
                onDiscardDraft = {
                    pendingDiscardId = workout.id
                },
                onEdit = {
                    onEditWorkout(workout.id)
                },
                canDelete = canDeleteTemplates && draft == null,
                onDelete = {
                    onDeleteClick(workout.id)
                },
                modifier = Modifier.fillMaxWidth().animateItem()
            )
        }
        item(key = "create_workout") {
            Button(
                onClick = onCreateWorkout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.workouts_add_workout)
                )
            }
        }
    }

    deleteState?.let { form ->
        AlertDialog(
            onDismissRequest = onDeleteDismiss,
            title = {
                Text(stringResource(R.string.delete_template_title))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.delete_template_description, form.workoutTitle)
                    )

                    form.errorMessage?.let { message ->
                        Text(
                            text = stringResource(message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDeleteConfirm,
                    enabled = !form.isDeleting && canDeleteTemplates
                ) {
                    Text(
                        text = if (form.isDeleting) {
                            stringResource(R.string.deleting)
                        } else {
                            stringResource(R.string.delete)
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDeleteDismiss,
                    enabled = !form.isDeleting
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val workoutToDiscard = uiState.workouts.firstOrNull { workout ->
        workout.id == pendingDiscardId
    }

    if (workoutToDiscard != null) {
        AlertDialog(
            onDismissRequest = {
                pendingDiscardId = null
            },
            title = {
                Text(stringResource(R.string.discard_draft_title))
            },
            text = {
                Text(
                    stringResource(R.string.discard_draft_description, workoutToDiscard.title)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDiscardDraft(workoutToDiscard.id)
                        pendingDiscardId = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingDiscardId = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    renameState?.let { form ->
        AlertDialog(
            onDismissRequest = onRenameDismiss,
            title = {
                Text(stringResource(R.string.rename_workout))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = form.title,
                        onValueChange = onRenameTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !form.isSaving,
                        singleLine = true,
                        isError = form.errorMessage != null,
                        label = {
                            Text(stringResource(R.string.create_workout_name))
                        }
                    )

                    form.errorMessage?.let { message ->
                        Text(
                            text = stringResource(message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onRenameConfirm,
                    enabled = !form.isSaving &&
                            form.title.isNotBlank()
                ) {
                    Text(
                        if (form.isSaving) {
                            stringResource(R.string.create_workout_saving)
                        } else {
                            stringResource(R.string.save)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onRenameDismiss,
                    enabled = !form.isSaving
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
