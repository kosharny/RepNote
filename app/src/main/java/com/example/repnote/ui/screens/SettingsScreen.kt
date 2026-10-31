package com.example.repnote.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.repnote.R
import com.example.repnote.model.AppLanguage
import com.example.repnote.model.ThemeMode
import com.example.repnote.ui.state.ResetDataUiState
import com.example.repnote.ui.state.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onLanguageChange: (AppLanguage) -> Unit = {},
    resetState: ResetDataUiState = ResetDataUiState.Idle,
    canReset: Boolean = true,
    onReset: () -> Unit = {},
    onAcknowledgeReset: () -> Unit = {}
) {
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    val resetting = resetState == ResetDataUiState.Saving
    val canChange = !uiState.isLoading && !uiState.isSaving &&
        uiState.loadErrorMessage == null && !resetting

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = {
                if (showBackButton) IconButton(onClick = onBack, enabled = !resetting) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.settings_intro), style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            SettingsSection(stringResource(R.string.appearance)) {
                ThemeMode.entries.forEach { mode ->
                    val title = stringResource(when (mode) {
                        ThemeMode.SYSTEM -> R.string.system_theme
                        ThemeMode.LIGHT -> R.string.light_theme
                        ThemeMode.DARK -> R.string.dark_theme
                    })
                    SettingsChoice(title, uiState.themeMode == mode, canChange) { onThemeModeChange(mode) }
                }
            }
            SettingsSection(stringResource(R.string.language)) {
                AppLanguage.entries.forEach { language ->
                    SettingsChoice(
                        if (language == AppLanguage.SYSTEM) stringResource(R.string.system_language) else language.nativeName,
                        uiState.language == language, canChange
                    ) { onLanguageChange(language) }
                }
            }
            if (uiState.isLoading || uiState.isSaving || resetting) LinearProgressIndicator(Modifier.fillMaxWidth())
            uiState.loadErrorMessage?.let {
                Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            }
            uiState.saveErrorMessage?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.local_data), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.local_data_description), style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = { confirmReset = true }, enabled = canChange && canReset,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text(stringResource(R.string.reset_all))
                    }
                }
            }
            Text(stringResource(R.string.about_repnote), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (confirmReset || resetting || resetState == ResetDataUiState.Error) {
        AlertDialog(
            onDismissRequest = { if (!resetting) { confirmReset = false; onAcknowledgeReset() } },
            title = { Text(stringResource(R.string.reset_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.reset_description))
                    if (resetting) LinearProgressIndicator(Modifier.fillMaxWidth())
                    if (resetState == ResetDataUiState.Error) Text(stringResource(R.string.reset_error), color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = { confirmReset = false; onReset() }, enabled = !resetting && canReset) {
                    Text(stringResource(R.string.reset_all), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false; onAcknowledgeReset() }, enabled = !resetting) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (resetState == ResetDataUiState.Success) {
        AlertDialog(onDismissRequest = onAcknowledgeReset,
            title = { Text(stringResource(R.string.reset_success)) },
            text = { Text(stringResource(R.string.reset_success_description)) },
            confirmButton = { TextButton(onClick = onAcknowledgeReset) { Text(stringResource(R.string.done)) } })
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.fillMaxWidth().animateContentSize().padding(20.dp).selectableGroup()) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SettingsChoice(title: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
        .padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RadioButton(selected, onClick = null, enabled = enabled)
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}
