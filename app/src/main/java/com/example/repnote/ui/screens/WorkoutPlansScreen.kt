package com.example.repnote.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.data.plans.WorkoutPlanCatalog
import com.example.repnote.ui.state.WorkoutPlansUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlansScreen(
    state: WorkoutPlansUiState,
    onPlanClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.workout_plans)) }, windowInsets = WindowInsets(0, 0, 0, 0))
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item(key = "intro") {
                Text(stringResource(R.string.plans_intro), style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (state.isLoading) item(key = "loading") { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            state.loadErrorMessage?.let { error -> item(key = "error") {
                Text(stringResource(error), color = MaterialTheme.colorScheme.error)
                OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            } }
            items(WorkoutPlanCatalog.plans, key = { it.key }) { plan ->
                Card(onClick = { onPlanClick(plan.key) }, modifier = Modifier.fillMaxWidth().animateItem(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Image(painterResource(plan.artworkRes), contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Fit)
                    Column(Modifier.fillMaxWidth().animateContentSize().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(stringResource(plan.titleRes), style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(R.string.plan_days_summary, plan.workouts.size),
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(plan.descriptionRes), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(if (plan.key in state.installedPlanKeys) R.string.plan_added else R.string.plan_explore),
                            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
