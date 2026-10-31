package com.example.repnote.ui.navigation

import com.example.repnote.model.AppLanguage
import com.example.repnote.ui.state.ResetDataUiState
import com.example.repnote.ui.viewmodel.RestTimerViewModel
import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.repnote.model.ThemeMode
import com.example.repnote.ui.components.DraftStorageContainer
import com.example.repnote.ui.screens.WorkoutPlansRoute
import com.example.repnote.ui.screens.ExerciseEntryRoute
import com.example.repnote.ui.screens.WorkoutDetailsScreen
import com.example.repnote.ui.screens.WorkoutResultRoute
import com.example.repnote.ui.screens.WorkoutsScreen
import com.example.repnote.ui.state.FinishWorkoutUiState
import com.example.repnote.ui.state.countCompletedSets
import com.example.repnote.ui.viewmodel.WorkoutDraftViewModel
import com.example.repnote.ui.screens.WorkoutHistoryRoute
import com.example.repnote.ui.viewmodel.WorkoutsViewModel
import com.example.repnote.ui.screens.CreateWorkoutRoute
import com.example.repnote.ui.screens.ExerciseCatalogRoute
import com.example.repnote.ui.screens.SettingsScreen
import com.example.repnote.ui.state.SettingsUiState
import androidx.navigation3.runtime.NavKey
import com.example.repnote.ui.components.AppStartupContainer

@Composable
fun RepNoteNavigation(
    settingsState: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onRetrySettings: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(WorkoutsDestination)
    val timer: RestTimerViewModel = viewModel(factory = RestTimerViewModel.Factory)
    val timerState by timer.uiState.collectAsStateWithLifecycle()
    val timerWorkoutId = when (val destination = backStack.lastOrNull()) {
        is WorkoutDetailsDestination -> destination.workoutId
        is ExerciseEntryDestination -> destination.workoutId
        else -> null
    }
    LaunchedEffect(timerWorkoutId) { timerWorkoutId?.let(timer::setWorkout) }
    val draftViewModel: WorkoutDraftViewModel = viewModel(
        factory = WorkoutDraftViewModel.Factory
    )
    val resetState by draftViewModel.resetState.collectAsStateWithLifecycle()
    val drafts by draftViewModel.drafts.collectAsStateWithLifecycle()
    val finishState by draftViewModel.finishState.collectAsStateWithLifecycle()

    val workoutsViewModel: WorkoutsViewModel = viewModel(
        factory = WorkoutsViewModel.factory()
    )

    val workoutsState by workoutsViewModel.uiState.collectAsStateWithLifecycle()

    val renameState by
    workoutsViewModel.renameState.collectAsStateWithLifecycle()

    val deleteState by
    workoutsViewModel.deleteState.collectAsStateWithLifecycle()

    val draftStorageState by
    draftViewModel.storageState.collectAsStateWithLifecycle()

    val canDeleteTemplates =
        !draftStorageState.isLoading &&
                !draftStorageState.isSaving &&
                draftStorageState.loadErrorMessage == null &&
                draftStorageState.saveErrorMessage == null

    LaunchedEffect(finishState) {
        val success = finishState as? FinishWorkoutUiState.Success
        if (success != null) {
            timer.reset()
            backStack[backStack.lastIndex] =
                WorkoutResultDestination(success.sessionId)

            draftViewModel.acknowledgeFinishSuccess()
        }
    }

    fun selectTab(destination: NavKey) {
        val rootDestinations = setOf(
            WorkoutsDestination,
            WorkoutPlansDestination,
            WorkoutHistoryDestination,
            SettingsDestination
        )

        if (resetState == ResetDataUiState.Saving) return
        if (destination !in rootDestinations) return
        if (backStack.lastOrNull() !in rootDestinations) return
        if (backStack.lastOrNull() == destination) return
        if (finishState == FinishWorkoutUiState.Saving) return

        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }

        if (destination != WorkoutsDestination) {
            backStack.add(destination)
        }
    }


    AppStartupContainer(
        settingsState = settingsState,
        draftState = draftStorageState,
        workoutsState = workoutsState,
        onRetrySettings = onRetrySettings,
        onRetryDrafts = draftViewModel::loadDrafts,
        onRetryWorkouts = workoutsViewModel::reload,
        modifier = modifier
    ) {
        DraftStorageContainer(
            state = draftStorageState,
            onRetryLoad = draftViewModel::loadDrafts,
            onRetrySave = draftViewModel::retryDraftSave,
            modifier = Modifier.fillMaxSize()
        ) {
            RepNoteNavigationLayout(
                currentDestination = backStack.lastOrNull(),
                onTabSelected = ::selectTab
            ) { navigationModifier ->
                NavDisplay(
                    modifier = navigationModifier,
                    backStack = backStack,
                    transitionSpec = {
                        (fadeIn(tween(240)) + slideInHorizontally(tween(300)) { it / 8 }) togetherWith
                            (fadeOut(tween(160)) + slideOutHorizontally(tween(300)) { -it / 12 })
                    },
                    popTransitionSpec = {
                        (fadeIn(tween(240)) + slideInHorizontally(tween(300)) { -it / 12 }) togetherWith
                            (fadeOut(tween(160)) + slideOutHorizontally(tween(300)) { it / 8 })
                    },
                    onBack = {
                        if (finishState != FinishWorkoutUiState.Saving &&
                            resetState != ResetDataUiState.Saving &&
                            backStack.size > 1
                        ) {
                            backStack.removeLastOrNull()
                        }
                    },
                    entryProvider = entryProvider {
                        entry<WorkoutsDestination> {
                            WorkoutsScreen(
                                uiState = workoutsState,
                                onWorkoutClick = { workoutId ->
                                    backStack.add(
                                        WorkoutDetailsDestination(workoutId)
                                    )
                                },
                                onCreateWorkout = {
                                    if (backStack.lastOrNull() != CreateWorkoutDestination) {
                                        backStack.add(CreateWorkoutDestination)
                                    }
                                },
                                onRetry = workoutsViewModel::reload,
                                renameState = renameState,
                                onRenameClick = workoutsViewModel::startRename,
                                onRenameTitleChange = workoutsViewModel::changeRenameTitle,
                                onRenameConfirm = workoutsViewModel::saveRename,
                                onRenameDismiss = workoutsViewModel::dismissRename,
                                drafts = drafts,
                                onDiscardDraft = draftViewModel::discardDraft,
                                onEditWorkout = { workoutId ->
                                    val destination = EditWorkoutDestination(workoutId)

                                    if (
                                        workoutId !in drafts &&
                                        backStack.lastOrNull() != destination
                                    ) {
                                        backStack.add(destination)
                                    }
                                },
                                deleteState = deleteState,
                                canDeleteTemplates = canDeleteTemplates,
                                onDeleteClick = { workoutId ->
                                    if (
                                        canDeleteTemplates &&
                                        workoutId !in drafts
                                    ) {
                                        workoutsViewModel.startDeleteTemplate(workoutId)
                                    }
                                },
                                onDeleteConfirm = {
                                    val workoutId = deleteState?.workoutId

                                    if (
                                        workoutId != null &&
                                        canDeleteTemplates &&
                                        workoutId !in drafts
                                    ) {
                                        workoutsViewModel.confirmDeleteTemplate()
                                    }
                                },
                                onDeleteDismiss = workoutsViewModel::dismissDeleteTemplate,
                                onBrowsePlans = { selectTab(WorkoutPlansDestination) },
                                onCatalogClick = {
                                    if (backStack.lastOrNull() != ExerciseCatalogDestination) {
                                        backStack.add(ExerciseCatalogDestination)
                                    }
                                },
                            )
                        }

                        entry<WorkoutPlansDestination> {
                            WorkoutPlansRoute(
                                onPlanClick = { key -> backStack.add(WorkoutPlanDetailsDestination(key)) },
                                onAdded = {}, onBack = {}
                            )
                        }
                        entry<WorkoutPlanDetailsDestination> { destination ->
                            WorkoutPlansRoute(
                                planKey = destination.planKey, onPlanClick = {},
                                onAdded = { while (backStack.size > 1) backStack.removeLastOrNull() },
                                onBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                            )
                        }
                        entry<CreateWorkoutDestination> {
                            CreateWorkoutRoute(
                                onCreated = {
                                    if (
                                        backStack.size > 1 &&
                                        backStack.lastOrNull() == CreateWorkoutDestination
                                    ) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                                onBack = {
                                    if (
                                        backStack.size > 1 &&
                                        backStack.lastOrNull() == CreateWorkoutDestination
                                    ) {
                                        backStack.removeLastOrNull()
                                    }
                                }
                            )
                        }

                        entry<WorkoutDetailsDestination> { destination ->
                            val workout = workoutsState.workouts.firstOrNull { workout ->
                                workout.id == destination.workoutId
                            }
                            val completedSetCount =
                                countCompletedSets(drafts[destination.workoutId])

                            WorkoutDetailsScreen(
                                workout = workout,
                                onBack = {
                                    if (finishState != FinishWorkoutUiState.Saving &&
                                        backStack.size > 1
                                    ) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                                onExerciseClick = { exerciseId ->
                                    backStack.add(
                                        ExerciseEntryDestination(
                                            workoutId = destination.workoutId,
                                            exerciseId = exerciseId
                                        )
                                    )
                                },
                                completedSetCount = completedSetCount,
                                onFinish = {
                                    if (workout != null) {
                                        draftViewModel.finishWorkout(workout)
                                    }
                                },
                                finishState = finishState,
                                completedByExercise = drafts[destination.workoutId]?.entriesByExercise.orEmpty()
                                    .mapValues { (_, entries) -> entries.count { it.isCompleted } },
                                timerState = timerState,
                                onTimerDuration = timer::selectDuration,
                                onTimerStart = timer::start,
                                onTimerPause = timer::pause,
                                onTimerReset = timer::reset
                            )
                        }
                        entry<ExerciseEntryDestination> { destination ->
                            val workout = workoutsState.workouts.firstOrNull { workout ->
                                workout.id == destination.workoutId
                            }
                            val exercise = workout?.exercises?.firstOrNull { exercise ->
                                exercise.id == destination.exerciseId
                            }
                            if (exercise != null) {
                                ExerciseEntryRoute(
                                    timer = timer,
                                    onNext = workout.exercises.indexOfFirst { it.id == exercise.id }.let { index ->
                                        workout.exercises.getOrNull(index + 1)?.let { next ->
                                            { backStack[backStack.lastIndex] = ExerciseEntryDestination(destination.workoutId, next.id) }
                                        }
                                    },
                                    exerciseName = exercise.name,
                                    exerciseNote = exercise.note,
                                    workoutId = destination.workoutId,
                                    exerciseId = destination.exerciseId,
                                    viewModel = draftViewModel,
                                    initialSetCount =
                                        workout?.initialSetCounts?.get(destination.exerciseId) ?: 3,
                                    onBack = {
                                        if (
                                            finishState != FinishWorkoutUiState.Saving &&
                                            backStack.size > 1 &&
                                            backStack.lastOrNull() == destination
                                        ) {
                                            backStack.removeLastOrNull()
                                        }
                                    },
                                )
                            } else {
                                Text(stringResource(R.string.exercise_not_found))
                            }
                        }
                        entry<WorkoutResultDestination> { destination ->

                            WorkoutResultRoute(
                                sessionId = destination.sessionId,
                                onBack = {
                                    backStack.removeLastOrNull()
                                },
                            )
                        }
                        entry<WorkoutHistoryDestination> { destination ->

                            WorkoutHistoryRoute(
                                onSessionClick = { sessionId ->
                                    backStack.add(
                                        WorkoutResultDestination(sessionId)
                                    )
                                },
                                onBack = {
                                    if (finishState != FinishWorkoutUiState.Saving &&
                                        backStack.size > 1
                                    ) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                                showBackButton = false,
                            )
                        }
                        entry<SettingsDestination> {
                            SettingsScreen(
                                uiState = settingsState,
                                onThemeModeChange = onThemeModeChange,
                                onLanguageChange = onLanguageChange,
                                resetState = resetState,
                                canReset = !draftStorageState.isSaving && !settingsState.isSaving,
                                onReset = draftViewModel::resetAllData,
                                onAcknowledgeReset = draftViewModel::acknowledgeReset,
                                onRetry = onRetrySettings,
                                onBack = {
                                    if (backStack.size > 1) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                                showBackButton = false,
                            )
                        }
                        entry<EditWorkoutDestination> { destination ->
                            val closeEditor: () -> Unit = {
                                if (
                                    backStack.size > 1 &&
                                    backStack.lastOrNull() == destination
                                ) {
                                    backStack.removeLastOrNull()
                                }
                            }

                            CreateWorkoutRoute(
                                templateId = destination.workoutId,
                                onCreated = closeEditor,
                                onBack = closeEditor
                            )
                        }
                        entry<ExerciseCatalogDestination> {
                            ExerciseCatalogRoute(
                                onBack = {
                                    if (
                                        backStack.size > 1 &&
                                        backStack.lastOrNull() == ExerciseCatalogDestination
                                    ) {
                                        backStack.removeLastOrNull()
                                    }
                                }
                            )
                        }
                    },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                )
            }
        }
    }
}
