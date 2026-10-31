package com.example.repnote.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.data.plans.WorkoutPlanCatalog
import com.example.repnote.data.plans.resolve
import com.example.repnote.ui.viewmodel.WorkoutPlansViewModel

@Composable
fun WorkoutPlansRoute(
    onPlanClick: (String) -> Unit,
    onAdded: () -> Unit,
    onBack: () -> Unit,
    planKey: String? = null
) {
    val viewModel: WorkoutPlansViewModel = viewModel(factory = WorkoutPlansViewModel.factory(planKey))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val resources = LocalResources.current
    BackHandler(enabled = state.isImporting) { }
    LaunchedEffect(state.importedPlanKey) {
        if (state.importedPlanKey != null) onAdded()
    }
    if (planKey == null) {
        WorkoutPlansScreen(state, onPlanClick, viewModel::reload)
    } else {
        val plan = WorkoutPlanCatalog.find(planKey)
        WorkoutPlanDetailsScreen(
            plan = plan, state = state,
            onDayToggle = viewModel::toggleDay,
            onAdd = {
                if (plan != null && state.selectedDays.size == plan.workouts.size) {
                    viewModel.importPlan(plan.resolve(state.selectedDays) { resources.getString(it) })
                }
            },
            onRetry = viewModel::reload, onBack = onBack
        )
    }
}
