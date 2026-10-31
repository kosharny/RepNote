package com.example.repnote.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.repnote.ui.state.ExerciseCatalogUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCatalogScreen(
    uiState: ExerciseCatalogUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.create_workout_exercises))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        enabled = uiState.editor?.isSaving != true
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onAdd,
                        enabled = !uiState.isLoading &&
                                uiState.loadErrorMessage == null &&
                                uiState.editor == null
                    ) {
                        Text(stringResource(R.string.add))
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading) {
                item(key = "loading") {
                    CircularProgressIndicator()
                }
            }

            uiState.loadErrorMessage?.let { message ->
                item(key = "error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(message),
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            if (
                !uiState.isLoading &&
                uiState.loadErrorMessage == null &&
                uiState.exercises.isEmpty()
            ) {
                item(key = "empty") {
                    Text(stringResource(R.string.add_first_exercise))
                }
            }

            items(
                items = uiState.exercises,
                key = { exercise -> exercise.id }
            ) { exercise ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (exercise.note.isNotBlank()) {
                            Text(exercise.note)
                        }

                        Row {
                            TextButton(
                                onClick = { onEdit(exercise.id) }
                            ) {
                                Text(stringResource(R.string.change))
                            }

                            TextButton(
                                onClick = { onDelete(exercise.id) }
                            ) {
                                Text(
                                    text = stringResource(R.string.delete),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.editor?.let { editor ->
        AlertDialog(
            onDismissRequest = onDismissEditor,
            title = {
                Text(
                    when {
                        editor.isDeleting -> stringResource(R.string.delete_exercise_title)
                        editor.exerciseId == null -> stringResource(R.string.create_workout_new_exercise)
                        else -> stringResource(R.string.edit_exercise_title)
                    }
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (editor.isDeleting) {
                        Text(
                            stringResource(R.string.delete_exercise_description, editor.name)
                        )
                    } else {
                        OutlinedTextField(
                            value = editor.name,
                            onValueChange = onNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !editor.isSaving,
                            singleLine = true,
                            label = { Text(stringResource(R.string.name)) }
                        )

                        OutlinedTextField(
                            value = editor.note,
                            onValueChange = onNoteChange,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !editor.isSaving,
                            minLines = 2,
                            maxLines = 4,
                            label = { Text(stringResource(R.string.note_optional)) }
                        )
                    }

                    editor.errorMessage?.let { message ->
                        Text(
                            text = stringResource(message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = if (editor.isDeleting) {
                        onConfirmDelete
                    } else {
                        onSave
                    },
                    enabled = !editor.isSaving &&
                            (editor.isDeleting || editor.name.isNotBlank())
                ) {
                    Text(
                        when {
                            editor.isSaving -> stringResource(R.string.working)
                            editor.isDeleting -> stringResource(R.string.delete)
                            else -> stringResource(R.string.save)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissEditor,
                    enabled = !editor.isSaving
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
