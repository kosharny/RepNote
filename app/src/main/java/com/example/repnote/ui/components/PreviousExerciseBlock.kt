package com.example.repnote.ui.components

import com.example.repnote.ui.util.formatWeight
import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.repnote.ui.state.PreviousExerciseUiState
import com.example.repnote.ui.util.formatSessionDate

@Composable
fun PreviousExerciseBlock(
    state: PreviousExerciseUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ) {
        when (val state = state) {
            PreviousExerciseUiState.Loading -> {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_previous))
                }
                    }

            PreviousExerciseUiState.Empty -> {
                    Text(stringResource(R.string.first_time))
            }

            is PreviousExerciseUiState.Error -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(state.message))
                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.retry))
                        }
                    }
            }

            is PreviousExerciseUiState.Content -> {
                val result = state.result
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.previous_result))
                    Text(
                        text = result.exercise.exerciseName,
                    )
                    Text(
                        text = formatSessionDate(
                            epochMillis = result.completedAtEpochMillis,
                            timeZoneId = result.timeZoneId
                        )
                    )

                    Column() {
                        result.exercise.sets.forEachIndexed { index, set ->
                            Text(stringResource(R.string.previous_set, index + 1, formatWeight(set.weightKg), set.reps))
                        }
                    }
                }

            }
        }

    }

}
