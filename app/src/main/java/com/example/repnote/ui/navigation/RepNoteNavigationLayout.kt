package com.example.repnote.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.repnote.R

private data class MainTab(val destination: NavKey, val title: Int, val icon: ImageVector)

private val mainTabs = listOf(
    MainTab(WorkoutsDestination, R.string.workouts, Icons.Default.Home),
    MainTab(WorkoutPlansDestination, R.string.workout_plans, Icons.Default.Star),
    MainTab(WorkoutHistoryDestination, R.string.history, Icons.AutoMirrored.Filled.List),
    MainTab(SettingsDestination, R.string.settings, Icons.Default.Settings)
)

@Composable
fun RepNoteNavigationLayout(
    currentDestination: NavKey?,
    onTabSelected: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    val showBottomBar = mainTabs.any { it.destination == currentDestination }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val labelWidth = (maxWidth / mainTabs.size - 8.dp).coerceAtLeast(40.dp)
                    NavigationBar(windowInsets = WindowInsets(0, 0, 0, 0)) {
                        mainTabs.forEach { tab ->
                            NavigationBarItem(
                                selected = currentDestination == tab.destination,
                                onClick = { onTabSelected(tab.destination) },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = {
                                    Text(
                                        stringResource(tab.title),
                                        modifier = Modifier.widthIn(max = labelWidth),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        content(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding))
    }
}
