package com.example.repnote.ui.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.repnote.data.plans.WorkoutPlanCatalog
import com.example.repnote.ui.state.WorkoutPlansUiState
import com.example.repnote.ui.theme.RepNoteTheme

@Preview(name = "Планы", locale = "ru", showBackground = true)
@Preview(name = "Планы — тёмная тема", locale = "uk", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WorkoutPlansPreview() {
    RepNoteTheme { WorkoutPlansScreen(WorkoutPlansUiState(isLoading = false), {}, {}) }
}

@Preview(name = "Расписание Full Body", locale = "ru", showBackground = true)
@Composable
private fun WorkoutPlanDetailsPreview() {
    RepNoteTheme {
        WorkoutPlanDetailsScreen(WorkoutPlanCatalog.plans.first(),
            WorkoutPlansUiState(isLoading = false, selectedDays = listOf(1, 3, 5)), {}, {}, {}, {})
    }
}
