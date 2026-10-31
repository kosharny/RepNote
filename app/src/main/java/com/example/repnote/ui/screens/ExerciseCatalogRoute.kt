package com.example.repnote.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.ui.viewmodel.ExerciseCatalogViewModel

@Composable
fun ExerciseCatalogRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ExerciseCatalogViewModel = viewModel(
        factory = ExerciseCatalogViewModel.Factory
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseCatalogScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::reload,
        onAdd = { viewModel.startEdit() },
        onEdit = { viewModel.startEdit(it) },
        onDelete = viewModel::startDelete,
        onNameChange = viewModel::changeName,
        onNoteChange = viewModel::changeNote,
        onSave = viewModel::save,
        onConfirmDelete = viewModel::delete,
        onDismissEditor = viewModel::dismissEditor,
        modifier = modifier
    )
}