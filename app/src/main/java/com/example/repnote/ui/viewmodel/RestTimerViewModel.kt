package com.example.repnote.ui.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.repnote.RepNoteApplication
import com.example.repnote.data.repository.SettingsRepository
import com.example.repnote.model.RestTimerSnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class RestTimerUiState(
    val durationSeconds: Int = 90,
    val remainingSeconds: Int = 90,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

class RestTimerViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RestTimerUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()
    private var snapshot = RestTimerSnapshot()
    private var ticker: Job? = null
    private var loadJob: Job? = null

    init { reload() }

    fun reload() {
        if (loadJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(isLoading = true, hasError = false)
        loadJob = viewModelScope.launch {
            try {
                repository.observeRestTimer().distinctUntilChanged().collect {
                    snapshot = it
                    ticker?.cancel()
                    refresh()
                    if (_uiState.value.isRunning) ticker = viewModelScope.launch {
                        while (_uiState.value.isRunning) { delay(200); refresh() }
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
            }
        }
    }

    private fun remaining(value: RestTimerSnapshot): Int {
        val deadline = value.deadlineElapsedMillis ?: return value.remainingSeconds
        if (value.bootCount != repository.bootCount) return value.durationSeconds
        return ((deadline - SystemClock.elapsedRealtime() + 999) / 1000).coerceIn(0, value.durationSeconds.toLong()).toInt()
    }

    private fun refresh() {
        val seconds = remaining(snapshot)
        val validDeadline = snapshot.deadlineElapsedMillis != null && snapshot.bootCount == repository.bootCount
        _uiState.value = RestTimerUiState(snapshot.durationSeconds, seconds,
            isRunning = validDeadline && seconds > 0, isFinished = seconds == 0)
    }

    private fun update(transform: (RestTimerSnapshot) -> RestTimerSnapshot) {
        viewModelScope.launch {
            try {
                repository.updateRestTimer(transform)
                _uiState.value = _uiState.value.copy(hasError = false)
                if (loadJob?.isActive != true) reload()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(hasError = true)
            }
        }
    }

    fun setWorkout(id: Long) = update { value ->
        if (value.workoutId == id) value else RestTimerSnapshot(workoutId = id, durationSeconds = value.durationSeconds,
            remainingSeconds = value.durationSeconds)
    }

    fun selectDuration(seconds: Int) {
        if (seconds !in listOf(60, 90, 120)) return
        update { it.copy(durationSeconds = seconds, remainingSeconds = seconds, deadlineElapsedMillis = null) }
    }

    fun start() = update {
        val seconds = remaining(it).takeIf { seconds -> seconds > 0 } ?: it.durationSeconds
        it.copy(remainingSeconds = seconds, deadlineElapsedMillis = SystemClock.elapsedRealtime() + seconds * 1000L,
            bootCount = repository.bootCount)
    }

    fun startAfterSet() = update {
        it.copy(remainingSeconds = it.durationSeconds,
            deadlineElapsedMillis = SystemClock.elapsedRealtime() + it.durationSeconds * 1000L, bootCount = repository.bootCount)
    }

    fun pause() = update { it.copy(remainingSeconds = remaining(it), deadlineElapsedMillis = null) }
    fun reset() = update { it.copy(remainingSeconds = it.durationSeconds, deadlineElapsedMillis = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RestTimerViewModel((this[APPLICATION_KEY] as RepNoteApplication).settingsRepository) }
        }
    }
}
