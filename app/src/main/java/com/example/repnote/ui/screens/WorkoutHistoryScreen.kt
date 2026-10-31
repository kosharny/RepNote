package com.example.repnote.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.ui.components.TrainingArtwork
import com.example.repnote.ui.state.*
import com.example.repnote.ui.util.formatSessionDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    uiState: WorkoutHistoryUiState,
    onSessionClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    deleteState: DeleteWorkoutUiState,
    onDeleteSession: (String) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    query: String = "",
    period: HistoryPeriod = HistoryPeriod.ALL,
    onQueryChange: (String) -> Unit = {},
    onPeriodChange: (HistoryPeriod) -> Unit = {}
) {
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val deleting = deleteState is DeleteWorkoutUiState.Deleting
    Column(modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.history)) },
            navigationIcon = {
                if (showBackButton) IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                }
            }, windowInsets = WindowInsets(0, 0, 0, 0))
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (uiState is WorkoutHistoryUiState.Content) {
                item(key = "summary") {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(stringResource(R.string.your_consistency), style = MaterialTheme.typography.titleLarge)
                            Text(stringResource(R.string.history_summary, uiState.totalSessions, uiState.activeDays),
                                style = MaterialTheme.typography.bodyLarge)
                            Text(stringResource(R.string.week_summary, uiState.sessionsThisWeek), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                item(key = "search") {
                    OutlinedTextField(value = query, onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        label = { Text(stringResource(R.string.search_workouts)) })
                }
                item(key = "period") {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HistoryPeriod.entries.forEach { value ->
                            FilterChip(selected = value == period, onClick = { onPeriodChange(value) },
                                label = { Text(stringResource(when (value) {
                                    HistoryPeriod.ALL -> R.string.all_time
                                    HistoryPeriod.WEEK -> R.string.last_7_days
                                    HistoryPeriod.MONTH -> R.string.last_30_days
                                })) })
                        }
                    }
                }
            }
            if (deleteState is DeleteWorkoutUiState.Error) item(key = "delete_error") {
                Text(stringResource(deleteState.message), color = MaterialTheme.colorScheme.error)
            }
            when (uiState) {
                WorkoutHistoryUiState.Loading -> item { CircularProgressIndicator() }
                is WorkoutHistoryUiState.Error -> item {
                    Text(stringResource(uiState.message), color = MaterialTheme.colorScheme.error)
                    Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                }
                is WorkoutHistoryUiState.Content -> {
                    if (uiState.sessions.isEmpty()) item(key = "empty") {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            TrainingArtwork(Modifier.fillMaxWidth().height(140.dp), com.example.repnote.ui.components.TrainingArtworkKind.HISTORY)
                            Text(stringResource(if (uiState.totalSessions == 0) R.string.history_empty else R.string.no_matches),
                                style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(if (uiState.totalSessions == 0) R.string.history_empty_hint else R.string.change_filters),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    items(uiState.sessions, key = { it.id }) { session ->
                        OutlinedCard(onClick = { onSessionClick(session.id) }, enabled = !deleting,
                            modifier = Modifier.fillMaxWidth().animateItem().animateContentSize()) {
                            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(session.workoutTitle, style = MaterialTheme.typography.titleMedium)
                                    Text(formatSessionDate(session.completedAtEpochMillis, session.timeZoneId),
                                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { pendingDeleteId = session.id }, enabled = !deleting) {
                                    Icon(Icons.Default.Delete, stringResource(R.string.delete_named, session.workoutTitle),
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            if ((deleteState as? DeleteWorkoutUiState.Deleting)?.sessionId == session.id) {
                                LinearProgressIndicator(Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }
    pendingDeleteId?.let { id ->
        AlertDialog(onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.delete_session_title)) },
            text = { Text(stringResource(R.string.delete_session_description)) },
            confirmButton = { TextButton(onClick = { pendingDeleteId = null; onDeleteSession(id) }, enabled = !deleting) {
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            } },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text(stringResource(R.string.cancel)) } })
    }
}
