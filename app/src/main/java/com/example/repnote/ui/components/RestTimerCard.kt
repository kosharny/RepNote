package com.example.repnote.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.ui.viewmodel.RestTimerUiState
import java.util.Locale

@Composable
fun RestTimerCard(
    state: RestTimerUiState,
    onDuration: (Int) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        state.remainingSeconds.toFloat() / state.durationSeconds, label = "restProgress"
    )
    val largeText = LocalDensity.current.fontScale > 1.3f
    Card(
        modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.rest_timer), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                if (!largeText) TrainingArtwork(Modifier.width(88.dp).height(56.dp), TrainingArtworkKind.REST)
            }
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (state.hasError) Text(stringResource(R.string.timer_error), color = MaterialTheme.colorScheme.error)
            if (largeText) {
                TimerDial(state, progress)
                TimerHint(state.isFinished)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TimerDial(state, progress)
                    Column(Modifier.weight(1f)) { TimerHint(state.isFinished) }
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(60, 90, 120).forEach { seconds ->
                    FilterChip(
                        selected = state.durationSeconds == seconds,
                        onClick = { onDuration(seconds) }, enabled = !state.isLoading,
                        label = { Text(stringResource(R.string.seconds_short, seconds)) }
                    )
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = if (state.isRunning) onPause else onStart, enabled = !state.isLoading) {
                    Text(stringResource(if (state.isRunning) R.string.pause else R.string.start_timer))
                }
                TextButton(onClick = onReset, enabled = !state.isLoading) { Text(stringResource(R.string.reset)) }
            }
        }
    }
}

@Composable
private fun TimerDial(state: RestTimerUiState, progress: Float) {
    val size = 88.dp * LocalDensity.current.fontScale.coerceAtMost(2f)
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(progress = { progress }, modifier = Modifier.size(size), strokeWidth = 5.dp)
        Text(
            String.format(Locale.ROOT, "%02d:%02d", state.remainingSeconds / 60, state.remainingSeconds % 60),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun TimerHint(finished: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AnimatedContent(finished, label = "timerStatus") { done ->
            Text(
                stringResource(if (done) R.string.rest_done else R.string.rest_hint),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(stringResource(R.string.timer_foreground), style = MaterialTheme.typography.bodySmall)
    }
}
