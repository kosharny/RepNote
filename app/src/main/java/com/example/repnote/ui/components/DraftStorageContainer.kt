package com.example.repnote.ui.components

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.repnote.ui.state.DraftStorageUiState

@Composable
fun DraftStorageContainer(
    state: DraftStorageUiState,
    onRetryLoad: () -> Unit,
    onRetrySave: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (state.isLoading || state.loadErrorMessage != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.loading_drafts))
            } else {
                Text(
                    text = stringResource(state.loadErrorMessage ?: R.string.error_load_drafts),
                    color = MaterialTheme.colorScheme.error
                )

                Button(onClick = onRetryLoad) {
                    Text(stringResource(R.string.retry))
                }
            }
        }

        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            if (state.isSaving) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.saveErrorMessage?.let { message ->
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(message),
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = onRetrySave,
                    enabled = !state.isSaving
                ) {
                    Text(stringResource(R.string.retry_save))
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}
