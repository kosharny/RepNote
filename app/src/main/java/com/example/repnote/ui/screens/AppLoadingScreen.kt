package com.example.repnote.ui.screens

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.ui.theme.RepNoteTheme

@Composable
fun AppLoadingScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.repnote_logo),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                if (errorMessage == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = stringResource(R.string.preparing),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = stringResource(R.string.prepare_error),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Загрузка — светлая",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AppLoadingLightPreview() {
    RepNoteTheme(darkTheme = false) {
        AppLoadingScreen(onRetry = {})
    }
}

@Preview(
    name = "Загрузка — темная",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AppLoadingDarkPreview() {
    RepNoteTheme(darkTheme = true) {
        AppLoadingScreen(onRetry = {})
    }
}

@Preview(
    name = "Ошибка подготовки",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AppLoadingErrorPreview() {
    RepNoteTheme(darkTheme = false) {
        AppLoadingScreen(
            errorMessage = stringResource(R.string.error_load_drafts),
            onRetry = {}
        )
    }
}
