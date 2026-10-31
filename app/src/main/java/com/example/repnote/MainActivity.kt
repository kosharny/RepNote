package com.example.repnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.SideEffect
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repnote.model.ThemeMode
import com.example.repnote.ui.navigation.RepNoteNavigation
import com.example.repnote.ui.theme.RepNoteTheme
import com.example.repnote.ui.viewmodel.SettingsViewModel

class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        (application as RepNoteApplication).settingsRepository.refreshLanguage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as RepNoteApplication).settingsRepository.refreshLanguage()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory
            )

            val settingsState by
            settingsViewModel.uiState.collectAsStateWithLifecycle()

            val darkTheme = when (settingsState.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            SideEffect {
                val style = if (darkTheme) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
            RepNoteTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    RepNoteNavigation(
                        settingsState = settingsState,
                        onThemeModeChange = settingsViewModel::changeThemeMode,
                        onRetrySettings = settingsViewModel::reload,
                        onLanguageChange = settingsViewModel::changeLanguage,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}
