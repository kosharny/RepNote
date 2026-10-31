package com.example.repnote.ui.viewmodel

import com.example.repnote.data.repository.SettingsRepository
import com.example.repnote.ui.state.ResetDataUiState
import com.example.repnote.R
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.WorkoutSessionRepository
import com.example.repnote.model.WorkoutSession
import com.example.repnote.model.WorkoutTemplate
import com.example.repnote.ui.state.DraftStorageUiState
import com.example.repnote.ui.state.FinishWorkoutUiState
import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.state.WorkoutDraftUiState
import com.example.repnote.ui.state.addSetEntry
import com.example.repnote.ui.state.removeSetEntry
import com.example.repnote.ui.state.toWorkoutSession
import com.example.repnote.ui.state.updateDraftEntries
import com.example.repnote.ui.state.updateStateCompletion
import com.example.repnote.ui.state.updateStateReps
import com.example.repnote.ui.state.updateStateWeight
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.util.TimeZone
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

private const val DRAFTS_KEY = "workout_drafts"
class WorkoutDraftViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val sessionRepository: WorkoutSessionRepository
) : ViewModel() {
    private val _drafts =
        MutableStateFlow<Map<Long, WorkoutDraftUiState>>(emptyMap())
    val drafts: StateFlow<Map<Long, WorkoutDraftUiState>> =
        _drafts.asStateFlow()

    private val _finishState =
        MutableStateFlow<FinishWorkoutUiState>(
            FinishWorkoutUiState.Idle
        )

    val finishState: StateFlow<FinishWorkoutUiState> =
        _finishState.asStateFlow()

    private val _storageState =
        MutableStateFlow(DraftStorageUiState())

    val storageState: StateFlow<DraftStorageUiState> =
        _storageState.asStateFlow()

    private val _resetState = MutableStateFlow<ResetDataUiState>(ResetDataUiState.Idle)
    val resetState = _resetState.asStateFlow()

    fun resetAllData() {
        if (!canChangeDrafts() || _storageState.value.isSaving) return
        _resetState.value = ResetDataUiState.Saving
        viewModelScope.launch {
            try {
                persistenceMutex.withLock {
                    sessionRepository.resetAllData()
                    updateDraftsState(emptyMap(), "{}")
                    lastSavedJson = "{}"
                    _storageState.update { it.copy(saveErrorMessage = null) }
                    settingsRepository.reset()
                }
                _resetState.value = ResetDataUiState.Success
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _resetState.value = ResetDataUiState.Error
            }
        }
    }

    fun acknowledgeReset() {
        if (_resetState.value != ResetDataUiState.Saving) {
            _resetState.value = ResetDataUiState.Idle
        }
    }

    private val persistenceMutex = Mutex()

    private var loadJob: Job? = null
    private var draftsLoaded = false
    private var pendingSaves = 0
    private var lastSavedJson: String? = null

    init {
        loadDrafts()
    }

    fun saveExerciseEntries(
        workoutId: Long,
        exerciseId: Long,
        entries: List<SetEntryUiState>
    ) {
        val draft = _drafts.value[workoutId] ?: WorkoutDraftUiState(workoutId = workoutId)
        val updatedDraft = updateDraftEntries(draft, exerciseId, entries)
        if (!canChangeDrafts()) return
        replaceDrafts(
            _drafts.value + (workoutId to updatedDraft)
        )
    }

    fun finishWorkout(workout: WorkoutTemplate) {
        if (!canChangeDrafts()) return

        val draft = _drafts.value[workout.id] ?: return

        val session = toWorkoutSession(
            workout = workout,
            draft = draft,
            sessionId = UUID.randomUUID().toString(),
            completedAtEpochMillis = System.currentTimeMillis(),
            timeZoneId = TimeZone.getDefault().id
        ) ?: return

        _finishState.value = FinishWorkoutUiState.Saving

        viewModelScope.launch {
            try {
                persistenceMutex.withLock {
                    val remainingDrafts =
                        _drafts.value - workout.id

                    val json =
                        Json.encodeToString(remainingDrafts)

                    sessionRepository.saveSession(
                        session = session,
                        draftsJson = json
                    )

                    updateDraftsState(remainingDrafts, json)
                    lastSavedJson = json

                    _storageState.update { current ->
                        current.copy(saveErrorMessage = null)
                    }
                }

                _finishState.value =
                    FinishWorkoutUiState.Success(session.id)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _finishState.value = FinishWorkoutUiState.Error(
                    message = R.string.error_save_workout
                )
            }
        }
    }

    fun acknowledgeFinishSuccess() {
        if (_finishState.value is FinishWorkoutUiState.Success) {
            _finishState.value = FinishWorkoutUiState.Idle
        }
    }

    fun ensureExercise(
        workoutId: Long,
        exerciseId: Long,
        initialSetCount: Int
    ) {
        val existingEntries =
            _drafts.value[workoutId]?.entriesByExercise?.get(exerciseId)

        if (existingEntries != null) return
        if (!canChangeDrafts()) return

        require(initialSetCount in 1..10) {
            "Некорректное количество начальных подходов"
        }

        val entries = List(initialSetCount) { index ->
            SetEntryUiState(
                id = index.toLong() + 1L
            )
        }

        saveExerciseEntries(
            workoutId = workoutId,
            exerciseId = exerciseId,
            entries = entries
        )
    }

    fun changeWeight(
        workoutId: Long,
        exerciseId: Long,
        entryId: Long,
        newWeight: String
    ) {
        updateExercise(workoutId, exerciseId) { entries ->
            updateStateWeight(entries, entryId, newWeight)
        }
    }

    fun changeReps(
        workoutId: Long,
        exerciseId: Long,
        entryId: Long,
        newReps: String
    ) {
        updateExercise(workoutId, exerciseId) { entries ->
            updateStateReps(entries, entryId, newReps)
        }
    }

    fun changeCompletion(
        workoutId: Long,
        exerciseId: Long,
        entryId: Long,
        isCompleted: Boolean
    ) {
        updateExercise(workoutId, exerciseId) { entries ->
            updateStateCompletion(entries, entryId, isCompleted)
        }
    }

    fun addEntry(
        workoutId: Long,
        exerciseId: Long,
    ) {
        updateExercise(workoutId, exerciseId) { entries ->
            addSetEntry(entries)
        }
    }

    fun removeEntry(
        workoutId: Long,
        exerciseId: Long,
        entryId: Long
    ) {
        updateExercise(workoutId, exerciseId) { entries ->
            removeSetEntry(entries, entryId)
        }
    }

    fun loadDrafts() {
        if (draftsLoaded || loadJob?.isActive == true) return

        _storageState.update { current ->
            current.copy(
                isLoading = true,
                loadErrorMessage = null
            )
        }

        loadJob = viewModelScope.launch {
            try {
                persistenceMutex.withLock {
                    val storedJson = sessionRepository.loadDraftsJson()

                    val json = storedJson
                        ?: savedStateHandle.get<String>(DRAFTS_KEY)
                        ?: "{}"

                    val restored =
                        Json.decodeFromString<
                                Map<Long, WorkoutDraftUiState>
                                >(json)

                    if (storedJson == null) {
                        sessionRepository.saveDraftsJson(json)
                    }

                    updateDraftsState(restored, json)
                    lastSavedJson = json
                    draftsLoaded = true
                }

                _storageState.update { current ->
                    current.copy(
                        isLoading = false,
                        loadErrorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _storageState.update { current ->
                    current.copy(
                        isLoading = false,
                        loadErrorMessage =
                            R.string.error_load_drafts
                    )
                }
            }
        }
    }

    private fun updateDraftsState(
        newDrafts: Map<Long, WorkoutDraftUiState>,
        json: String = Json.encodeToString(newDrafts)
    ) {
        savedStateHandle[DRAFTS_KEY] = json
        _drafts.value = newDrafts
    }

    private fun updateExercise(
        workoutId: Long,
        exerciseId: Long,
        transform: (List<SetEntryUiState>) -> List<SetEntryUiState>
    ) {
        if (!canChangeDrafts()) return

        val currentEntries =
            _drafts.value[workoutId]?.entriesByExercise?.get(exerciseId)
                ?: return

        val updatedEntries = transform(currentEntries)

        saveExerciseEntries(
            workoutId,
            exerciseId,
            updatedEntries
        )
    }

    private fun replaceDrafts(
        newDrafts: Map<Long, WorkoutDraftUiState>
    ) {
        if (newDrafts == _drafts.value) return

        updateDraftsState(newDrafts)
        persistCurrentDrafts()
    }

    private fun persistCurrentDrafts() {
        pendingSaves += 1

        _storageState.update { current ->
            current.copy(isSaving = true)
        }

        viewModelScope.launch {
            try {
                persistenceMutex.withLock {
                    val json = Json.encodeToString(_drafts.value)

                    if (json != lastSavedJson) {
                        sessionRepository.saveDraftsJson(json)
                        lastSavedJson = json
                    }
                }

                _storageState.update { current ->
                    current.copy(saveErrorMessage = null)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _storageState.update { current ->
                    current.copy(
                        saveErrorMessage =
                            R.string.error_save_draft
                    )
                }
            } finally {
                pendingSaves -= 1

                _storageState.update { current ->
                    current.copy(isSaving = pendingSaves > 0)
                }
            }
        }
    }

    fun discardDraft(workoutId: Long) {
        if (!canChangeDrafts()) return
        if (workoutId !in _drafts.value) return

        replaceDrafts(_drafts.value - workoutId)
    }


    fun retryDraftSave() {
        if (!canChangeDrafts() || _storageState.value.isSaving) return

        persistCurrentDrafts()
    }

    private fun canChangeDrafts(): Boolean {
        return draftsLoaded &&
                _resetState.value != ResetDataUiState.Saving &&
                _finishState.value != FinishWorkoutUiState.Saving &&
                _finishState.value !is FinishWorkoutUiState.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[APPLICATION_KEY] as RepNoteApplication

                WorkoutDraftViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    settingsRepository = application.settingsRepository,
                    sessionRepository = application.sessionRepository
                )
            }
        }
    }
}
