package com.example.repnote.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.repnote.model.ThemeMode
import com.example.repnote.ui.state.SettingsUiState
import com.example.repnote.ui.theme.RepNoteTheme

private val loadedSettingsState = SettingsUiState(isLoading = false)

@Composable
private fun SettingsPreview(
    uiState: SettingsUiState,
    darkTheme: Boolean = uiState.themeMode == ThemeMode.DARK
) {
    RepNoteTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen(
                uiState = uiState,
                onThemeModeChange = {},
                onRetry = {},
                onBack = {}
            )
        }
    }
}

@Preview(name = "Системная тема", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsSystemPreview() {
    SettingsPreview(loadedSettingsState)
}

@Preview(name = "Светлая тема", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsLightPreview() {
    SettingsPreview(loadedSettingsState.copy(themeMode = ThemeMode.LIGHT))
}

@Preview(name = "Темная тема", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsDarkPreview() {
    SettingsPreview(loadedSettingsState.copy(themeMode = ThemeMode.DARK))
}

@Preview(name = "Загрузка настроек", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsLoadingPreview() {
    SettingsPreview(SettingsUiState())
}

@Preview(name = "Сохранение темы", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsSavingPreview() {
    SettingsPreview(
        loadedSettingsState.copy(
            themeMode = ThemeMode.DARK,
            isSaving = true
        )
    )
}

@Preview(name = "Ошибка чтения", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsLoadErrorPreview() {
    SettingsPreview(
        loadedSettingsState.copy(
            loadErrorMessage = R.string.error_load_settings
        )
    )
}

@Preview(name = "Ошибка записи", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SettingsSaveErrorPreview() {
    SettingsPreview(
        loadedSettingsState.copy(
            themeMode = ThemeMode.LIGHT,
            saveErrorMessage = R.string.error_save_theme
        )
    )
}
