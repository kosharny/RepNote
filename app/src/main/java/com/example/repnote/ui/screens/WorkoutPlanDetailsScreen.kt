package com.example.repnote.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.data.plans.WorkoutPlanDefinition
import com.example.repnote.ui.state.WorkoutPlansUiState
import com.example.repnote.ui.util.weekdayNameRes
import com.example.repnote.ui.util.weekdayShortRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanDetailsScreen(
    plan: WorkoutPlanDefinition?,
    state: WorkoutPlansUiState,
    onDayToggle: (Int) -> Unit,
    onAdd: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val installed = plan?.key in state.installedPlanKeys
    val canAdd = plan != null && !installed && !state.isLoading && !state.isImporting &&
        state.loadErrorMessage == null && state.selectedDays.size == plan.workouts.size
    Scaffold(modifier.fillMaxSize(), contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text(stringResource(plan?.titleRes ?: R.string.workout_plans)) },
                navigationIcon = { IconButton(onClick = onBack, enabled = !state.isImporting) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                } }, windowInsets = WindowInsets(0, 0, 0, 0))
        },
        bottomBar = {
            if (plan != null) Surface(tonalElevation = 2.dp) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.errorMessage?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
                    Button(onClick = onAdd, enabled = canAdd, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(when {
                            installed -> R.string.plan_added
                            state.isImporting -> R.string.create_workout_saving
                            else -> R.string.plan_add_all
                        }))
                    }
                    if (state.isImporting) LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (plan == null) item { Text(stringResource(R.string.plan_not_found)) }
            else {
                item(key = "cover") {
                    Image(painterResource(plan.artworkRes), null, Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Fit)
                }
                item(key = "description") {
                    Text(stringResource(plan.descriptionRes), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.plan_editable_hint), style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.loadErrorMessage?.let { error -> item(key = "error") {
                    Text(stringResource(error), color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                } }
                item(key = "schedule") {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.fillMaxWidth().animateContentSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(stringResource(R.string.plan_schedule), style = MaterialTheme.typography.titleLarge)
                            if (installed) Text(stringResource(R.string.plan_already_added))
                            else Text(stringResource(R.string.plan_selected_days, state.selectedDays.size, plan.workouts.size))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (1..7).forEach { day ->
                                    FilterChip(selected = if (installed) day in state.installedSchedules[plan.key].orEmpty().values else day in state.selectedDays,
                                        onClick = { onDayToggle(day) },
                                        enabled = !installed && !state.isImporting && (day in state.selectedDays || state.selectedDays.size < plan.workouts.size),
                                        label = { Text(stringResource(weekdayShortRes(day))) })
                                }
                            }
                            Text(stringResource(if (installed) R.string.plan_original_preview else R.string.plan_schedule_hint), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                itemsIndexed(plan.workouts, key = { _, workout -> workout.titleRes }) { index, workout ->
                    OutlinedCard(Modifier.fillMaxWidth().animateItem()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val scheduledDay = if (installed) state.installedSchedules[plan.key]?.get(index) else state.selectedDays.getOrNull(index)
                            Text(scheduledDay?.let { stringResource(weekdayNameRes(it)) }
                                ?: stringResource(if (installed) R.string.plan_workout_removed else R.string.plan_choose_day), color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge)
                            Text(stringResource(workout.titleRes), style = MaterialTheme.typography.titleLarge)
                            Text(stringResource(R.string.plan_scheme_hint), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            workout.exercises.forEach { exercise ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(stringResource(exercise.nameRes), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                    Text(stringResource(R.string.plan_set_scheme, exercise.sets, stringResource(exercise.repsRes)),
                                        Modifier.weight(0.55f), style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
